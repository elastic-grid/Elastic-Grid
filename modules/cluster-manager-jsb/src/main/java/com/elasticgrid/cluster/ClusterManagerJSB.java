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
package com.elasticgrid.cluster;

import com.elasticgrid.cluster.spi.CloudPlatformManager;
import com.elasticgrid.model.Cluster;
import com.elasticgrid.model.ClusterException;
import com.elasticgrid.model.ClusterNotFoundException;
import org.rioproject.core.jsb.ServiceBeanContext;
import org.rioproject.jsb.ServiceBeanActivation;
import org.rioproject.jsb.ServiceBeanAdapter;
import org.rioproject.associations.AssociationDescriptor;
import org.rioproject.associations.AssociationType;
import org.rioproject.associations.AssociationMgmt;
import org.rioproject.associations.AssociationListener;
import org.rioproject.associations.Association;
import org.rioproject.monitor.ProvisionMonitor;
import com.sun.jini.start.LifeCycle;
import com.sun.jini.start.ClassLoaderUtil;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.entry.Entry;

/**
 * JSB exposing the {@link ClusterManager}.
 *
 * @author Jerome Bernard
 */
public class ClusterManagerJSB extends ServiceBeanAdapter implements ClusterManager {
    private static ClusterManager clusterManager;

    /**
     * Component name we use to find items in the Configuration
     */
    static final String EG_CONFIG_COMPONENT = "com.elasticgrid";
    /**
     * Component name we use to find items in the Configuration
     */
    static final String CONFIG_COMPONENT = EG_CONFIG_COMPONENT + ".cluster";
    /**
     * The componant name for accessing the service's configuration
     */
    static String configComponent = CONFIG_COMPONENT;
    /**
     * Logger name
     */
    static final String LOGGER = "com.elasticgrid.cluster";
    /**
     * Cluster Manager logger.
     */
    static final Logger logger = Logger.getLogger(LOGGER);

    /**
     * Create a {@link ClusterManager} launched from the ServiceStarter framework
     *
     * @param configArgs Configuration arguments
     * @param lifeCycle  The LifeCycle object that started the REST API
     * @throws Exception if bootstrapping fails
     */
    public ClusterManagerJSB(String[] configArgs, LifeCycle lifeCycle) throws Exception {
        super();
        bootstrap(configArgs);
    }

    /**
     * Get the ServiceBeanContext and bootstrap the {@link ClusterManager}.
     */
    protected void bootstrap(String[] configArgs) throws Exception {
        try {
            context = ServiceBeanActivation.getServiceBeanContext(
                    getConfigComponent(),
                    "Cluster Manager",
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

            // build the association with Cloud Platform Managers
            AssociationDescriptor cpmAssociation = new AssociationDescriptor(AssociationType.REQUIRES);
            cpmAssociation.setMatchOnName(false);
            cpmAssociation.setInterfaceNames(CloudPlatformManager.class.getName());
            cpmAssociation.setGroups(context.getServiceBeanConfig().getGroups());

            // register the association listener
            AssociationMgmt assocMgt = new AssociationMgmt();
            assocMgt.register(new CloudPlatformManagerListener());

            // search for the provision monitor
            assocMgt.addAssociationDescriptors(cpmAssociation);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Register to LifeCycleManager", e);
            throw e;
        }
    }

    @Override
    public void initialize(ServiceBeanContext context) throws Exception {
        super.initialize(context);
        clusterManager = new CloudFederationClusterManager();
    }

    @Override
    public void advertise() throws IOException {
        super.advertise();
        logger.info("Advertised Cluster Manager");
    }

    /**
     * Get the component name to use for accessing the services configuration properties
     *
     * @return The component name
     */
    public static String getConfigComponent() {
        return configComponent;
    }

    public void startCluster(String clusterName, List clusterTopology)
            throws ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
        clusterManager.startCluster(clusterName, clusterTopology);
    }

    public void stopCluster(String clusterName) throws ClusterException, RemoteException {
        clusterManager.stopCluster(clusterName);
    }

    public Set findClusters() throws ClusterException, RemoteException {
        return clusterManager.findClusters();
    }

    public Cluster cluster(String name) throws ClusterException, RemoteException {
        return clusterManager.cluster(name);
    }

    public void resizeCluster(String clusterName, List clusterTopology)
            throws ClusterNotFoundException, ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
        clusterManager.resizeCluster(clusterName, clusterTopology);
    }

    public static void setCloudPlatformManagers(List<CloudPlatformManager> clouds) {
        try {
            if (logger.isLoggable(Level.INFO)) {                // TODO: switch to CONFIG level instead
                for (CloudPlatformManager cloud : clouds)
                    logger.info(String.format("Cluster Manager discovered Cloud Platform %s", cloud.getName()));
            }
            ((CloudFederationClusterManager) clusterManager).setClouds(clouds);
        } catch (RemoteException e) {
            logger.log(Level.SEVERE, "Could not update list of cloud platform managers", e);
        }
    }

    /**
     * An AssociationListener for Provision Monitor instances
     */
    static class CloudPlatformManagerListener implements AssociationListener<CloudPlatformManager> {
        public void discovered(Association association, CloudPlatformManager cpm) {
            try {
                logger.info("Discovered cloud " + cpm.getName());
                ServiceItem[] serviceItems = association.getServiceItems();
                List<CloudPlatformManager> clouds = new ArrayList<CloudPlatformManager>(serviceItems.length);
                for (ServiceItem serviceItem : serviceItems) {
                    CloudPlatformManager cloud = (CloudPlatformManager) serviceItem.service;
                    if ("Private LAN".equals(cloud.getName()))
                        clouds.add(0, cloud);       // first one
                    else if ("Amazon EC2".equals(cloud.getName()))
                        clouds.add(cloud);          // last one
                    else
                        logger.warning(String.format("Can't cope with %s platform yet", cloud.getName()));
                }
                setCloudPlatformManagers(clouds);
            } catch (RemoteException e) {
                logger.log(Level.SEVERE, "Could not retreive cloud platform name", e);
            }
        }

        public void changed(Association association, CloudPlatformManager provisionMonitor) {
        }

        public void broken(Association association, CloudPlatformManager provisionMonitor) {
            setCloudPlatformManagers(null);
        }
    }
}
