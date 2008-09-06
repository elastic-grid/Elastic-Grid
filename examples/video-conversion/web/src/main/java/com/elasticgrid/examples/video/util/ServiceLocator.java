/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This file is part of Elastic Grid.
 * 
 * Elastic Grid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * Elastic Grid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Elastic Grid.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elasticgrid.examples.video.util;

import com.xerox.amazonws.ec2.EC2Exception;
import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.ReservationDescription;
import net.jini.config.ConfigurationException;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.DiscoveryManagement;
import net.jini.lookup.LookupCache;
import net.jini.lookup.ServiceItemFilter;
import org.apache.commons.io.IOUtils;
import org.rioproject.opstring.OpStringManagerProxy;
import org.rioproject.resources.client.DiscoveryManagementPool;
import org.rioproject.resources.client.LookupCachePool;
import org.rioproject.resources.servicecore.Service;
import org.rioproject.watch.WatchDataSource;
import org.rioproject.watch.Watchable;

import java.io.*;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.security.Permission;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServiceLocator {
    private static LookupCache lookupCache;
    private static String egHome = System.getProperty("EG_HOME");
    private static final Logger logger = Logger.getLogger(ServiceLocator.class.getName());

    static {
        System.setSecurityManager(new RMISecurityManager() {
            public void checkPermission(Permission perm) {
                // do nothing -- hence approve everything
            }
        });
        try {
            ServiceTemplate template = new ServiceTemplate(null, null, null);
            DiscoveryManagement discoMgr = DiscoveryManagementPool.getInstance().getDiscoveryManager(
                    null,
                    new String[]{"rio", "javaone"},
                    lookupMonitors()
            );
            lookupCache = LookupCachePool.getInstance().getLookupCache(discoMgr, template);
            while (lookupCache.lookup(null) == null) {
                System.out.printf("Waiting for service(s)...\n");
                Thread.sleep(1000);
            }
            OpStringManagerProxy.setDiscoveryManagement(discoMgr);
        } catch (ConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (EC2Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private static LookupLocator[] lookupMonitors() throws IOException, EC2Exception {
        Properties egParameters;

        try {
            // running on EC2
            Properties metadata = fetchLaunchParameters();
            egParameters = translateProperties(metadata);
        } catch (FileNotFoundException e) {
            // running on LAN
            File egConfiguration = new File(System.getProperty("user.home") + File.separator + ".eg", "aws.properties");
            egParameters = new Properties();
            egParameters.load(new FileInputStream(egConfiguration));
        }

        // figure out who is the monitor host
        System.out.printf("Searching for Elastic Grid monitor host...\n");
        Jec2 ec2 = new Jec2(
                egParameters.getProperty(EG_PARAMETER_ACCESS_ID), egParameters.getProperty(EG_PARAMETER_SECRET_KEY));
        List<ReservationDescription> reservations = ec2.describeInstances(Collections.<String>emptyList());
        for (ReservationDescription reservation : reservations) {
            List<String> groups = reservation.getGroups();
            if (groups.contains(EG_GROUP_MONITOR)) {
                // this is a monitor reservation -- instance
                List<ReservationDescription.Instance> instances = reservation.getInstances();
                if (instances.size() == 0)
                    continue;
                ReservationDescription.Instance monitor = null;
                for (ReservationDescription.Instance instance : instances) {
                    monitor = instances.get(0);
                    if ("running".equals(monitor.getState()))
                        break;
                }
                if (monitor == null) {
                    System.err.println("Could not find any monitor host. Aborting boot process.");
                    System.exit(-1);
                }
                if (instances.size() > 1) {
                    System.err.printf("More than once instance found (found %d). Using instance %s as monitor.\n",
                            instances.size(), monitor.getDnsName());
                }
                String monitorHost = monitor.getPrivateDnsName();
                System.out.println("Found monitor " + monitorHost);
                return new LookupLocator[] { new LookupLocator("jini://" + monitorHost) };
            }
        }
        System.err.println("Could not find monitor host!");
        return null;
    }

    public static ServiceItem getServiceByServiceID(final ServiceID serviceID) throws InterruptedException {
        return lookupCache.lookup(new ServiceItemFilter() {
            public boolean check(ServiceItem serviceItem) {
                return serviceItem.serviceID.equals(serviceID);
            }
        });
    }

    public static ServiceItem[] getServicesByClass(final Class serviceClazz) throws InterruptedException {
        return lookupCache.lookup(new ServiceItemFilter() {
            public boolean check(ServiceItem serviceItem) {
                boolean match = serviceClazz.isAssignableFrom(serviceItem.service.getClass());
                if (match)
                    logger.log(Level.INFO, "Found service {0}", serviceItem.serviceID);
                return match;
            }
        }, Integer.MAX_VALUE);
    }

    public static List<WatchDataSource> getWatchDataSourcesByServiceID(final ServiceID serviceID) throws RemoteException, InterruptedException {
        Watchable w = (Service) getServiceByServiceID(serviceID).service;
        return Arrays.asList(w.fetch());
    }

    public static Map<ServiceID, List<WatchDataSource>> getWatchDataSources(Class serviceClazz) throws InterruptedException, RemoteException {
        ServiceItem[] items = getServicesByClass(serviceClazz);
        Map<ServiceID, List<WatchDataSource>> watches = new HashMap<ServiceID, List<WatchDataSource>>();
        for (ServiceItem item : items) {
            watches.put(item.serviceID, Arrays.asList(((Service) item.service).fetch()));
        }
        return watches;
    }

    private static Properties fetchLaunchParameters() throws IOException {
        Properties launchProperties = new Properties();
        InputStream launchFile = null;
        try {
            File egConfiguration = new File(LAUNCH_PARAMETERS_FILE);
            launchFile = new FileInputStream(egConfiguration);
            launchProperties.load(launchFile);
            return launchProperties;
        } finally {
            IOUtils.closeQuietly(launchFile);
        }
    }

    private static Properties translateProperties(Properties launchParameters) {
        // translate properties
        Properties egParameters = new Properties();
        for (Map.Entry property : launchParameters.entrySet()) {
            String key = (String) property.getKey();
            if (LAUNCH_PARAMETER_ACCESS_ID.equals(key))
                egParameters.put(EG_PARAMETER_ACCESS_ID, property.getValue());
            if (LAUNCH_PARAMETER_SECRET_KEY.equals(key))
                egParameters.put(EG_PARAMETER_SECRET_KEY, property.getValue());
            if (LAUNCH_PARAMETER_SQS_SECURED.equals(key))
                egParameters.put(EG_PARAMETER_SQS_SECURED, property.getValue());
        }
        return egParameters;
    }

    public static final String LAUNCH_PARAMETER_ACCESS_ID = "AWS_ACCESS_ID";
    public static final String LAUNCH_PARAMETER_SECRET_KEY = "AWS_SECRET_KEY";
    public static final String LAUNCH_PARAMETER_SQS_SECURED = "AWS_SQS_SECURED";

    public static final String EG_PARAMETER_ACCESS_ID = "aws.accessId";
    public static final String EG_PARAMETER_SECRET_KEY = "aws.secretKey";
    public static final String EG_PARAMETER_SQS_SECURED = "aws.sqs.secured";

    public static final String EG_GROUP_MONITOR = "eg-monitor";
    public static final String EG_GROUP_CYBERNODE = "eg-cybernode";

    public static final String LAUNCH_PARAMETERS_FILE = "/tmp/user-data";
    public static final String ELASTIC_GRID_CONFIGURATION_FILE = "config/eg.properties";

}