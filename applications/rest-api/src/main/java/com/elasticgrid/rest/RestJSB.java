/**
 * Elastic Grid
 * Copyright (C) 2008-2009 Elastic Grid, LLC.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elasticgrid.rest;

import com.elasticgrid.cluster.CloudFederationClusterManager;
import com.elasticgrid.cluster.ClusterManager;
import com.elasticgrid.cluster.spi.CloudPlatformManager;
import com.elasticgrid.model.ec2.EC2Cluster;
import com.elasticgrid.model.lan.LANCluster;
import com.elasticgrid.platforms.ec2.EC2CloudPlatformManagerFactory;
import com.elasticgrid.platforms.lan.LANCloudPlatformManagerFactory;
import org.rioproject.associations.Association;
import org.rioproject.associations.AssociationDescriptor;
import org.rioproject.associations.AssociationListener;
import org.rioproject.associations.AssociationMgmt;
import org.rioproject.associations.AssociationType;
import org.rioproject.core.OperationalStringManager;
import org.rioproject.core.jsb.ServiceBeanContext;
import org.rioproject.jsb.ServiceBeanActivation;
import org.rioproject.jsb.ServiceBeanAdapter;
import org.rioproject.monitor.DeployAdmin;
import org.rioproject.monitor.ProvisionMonitor;
import com.sun.jini.start.LifeCycle;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;

/**
 * JSB exposing the underlying REST API.
 */
public class RestJSB extends ServiceBeanAdapter {
    private static CloudFederationClusterManager clusterManager;
    private static ProvisionMonitor provisionMonitor;
    /** Component name we use to find items in the Configuration */
    static final String EG_CONFIG_COMPONENT = "com.elasticgrid";
    /** Component name we use to find items in the Configuration */
    static final String CONFIG_COMPONENT = EG_CONFIG_COMPONENT + ".rest";
    /** The componant name for accessing the service's configuration */
    static String configComponent = CONFIG_COMPONENT;
    /** Logger name */
    static final String LOGGER = "com.elasticgrid.rest";
    /** REST API logger. */
    static final Logger logger = Logger.getLogger(LOGGER);

    /**
     * Create a REST API launched from the ServiceStarter framework
     *
     * @param configArgs Configuration arguments
     * @param lifeCycle The LifeCycle object that started the REST API
     *
     * @throws Exception if bootstrapping fails
     */
    public RestJSB(String[] configArgs, LifeCycle lifeCycle) throws Exception {
        super();
        bootstrap(configArgs);
    }

    /**
     * Get the ServiceBeanContext and bootstrap the REST API
     */
    protected void bootstrap(String[] configArgs) throws Exception {
        try {
            context = ServiceBeanActivation.getServiceBeanContext(
                                                  getConfigComponent(),
                                                  "REST",
                                                  configArgs,
                                                  getClass().getClassLoader());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Getting ServiceElement", e);
            throw e;
        }
        try {
            start(context);
            ServiceBeanActivation.LifeCycleManager lMgr =
                    (ServiceBeanActivation.LifeCycleManager) context.getServiceBeanManager().getDiscardManager();
            if (lMgr != null) {
                lMgr.register(getServiceProxy(), context);
            } else {
                logger.log(Level.WARNING, "LifeCycleManager is null, unable to register");
            }

            // build the association with the provision monitor to fulfill
            AssociationDescriptor provisionMonitorAssociation = new AssociationDescriptor(AssociationType.REQUIRES);
            provisionMonitorAssociation.setMatchOnName(false);
            provisionMonitorAssociation.setInterfaceNames(ProvisionMonitor.class.getName());
            provisionMonitorAssociation.setGroups(
                context.getServiceBeanConfig().getGroups());

            // register the association listener
            AssociationMgmt assocMgt = new AssociationMgmt();
            assocMgt.register(new ProvisionMonitorListener());

            // search for the provision monitor
            assocMgt.addAssociationDescriptors(provisionMonitorAssociation);
        } catch(Exception e) {
            logger.log(Level.SEVERE, "Register to LifeCycleManager", e);
            throw e;
        }
    }

    @Override
    public void initialize(ServiceBeanContext context) throws Exception {
        super.initialize(context);
        initializeClusterManager();
        new RestApplication();
    }

    public static void initializeClusterManager() throws IOException {
        clusterManager = new CloudFederationClusterManager();
        CloudPlatformManager<LANCluster> lanCloud = new LANCloudPlatformManagerFactory().getInstance();
        CloudPlatformManager<EC2Cluster> ec2Cloud = new EC2CloudPlatformManagerFactory().getInstance();
        clusterManager.setClouds(Arrays.asList(lanCloud, ec2Cloud));
    }

    /**
     * Get the component name to use for accessing the services configuration properties
     *
     * @return The component name
     */
    public static String getConfigComponent() {
        return configComponent;
    }

    public static ClusterManager getClusterManager() {
        return clusterManager;
    }

    public static ProvisionMonitor getProvisionMonitor() {
        return provisionMonitor;
    }

    public static List<OperationalStringManager> getOperationalStringManagers() throws RemoteException {
        if (provisionMonitor == null)
            return Collections.emptyList();
        DeployAdmin dAdmin = (DeployAdmin) provisionMonitor.getAdmin();
        OperationalStringManager[] operationalStringMgrs = dAdmin.getOperationalStringManagers();
        for (int i = 0; i < operationalStringMgrs.length; i++) {
            OperationalStringManager operationalStringMgr = operationalStringMgrs[i];
            logger.info("found opstring mgr " + operationalStringMgr);
        }
        logger.log(Level.INFO, "Found {0} opstrings", operationalStringMgrs.length);
        return Arrays.asList(operationalStringMgrs);
    }

    public static void setProvisionMonitor(ProvisionMonitor provisionMonitor) {
        RestJSB.provisionMonitor = provisionMonitor;
    }

    /**
     * An AssociationListener for Provision Monitor instances
     */
    static class ProvisionMonitorListener implements AssociationListener<ProvisionMonitor> {
        public void discovered(Association association, ProvisionMonitor provisionMonitor) {
            setProvisionMonitor(provisionMonitor);
        }

        public void changed(Association association, ProvisionMonitor provisionMonitor) {
        }

        public void broken(Association association, ProvisionMonitor provisionMonitor) {
            setProvisionMonitor(null);
        }
    }
}
