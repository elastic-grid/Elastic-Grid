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
import com.sun.jini.start.LifeCycle;
import com.sun.jini.start.ClassLoaderUtil;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JSB exposing the {@link ClusterManager}.
 *
 * @author Jerome Bernard
 */
public class ClusterManagerJSB extends ServiceBeanAdapter implements ClusterManager {
    private ClusterManager clusterManager;

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
        ClassLoaderUtil.displayContextClassLoaderTree();
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
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Register to LifeCycleManager", e);
            throw e;
        }
    }

    @Override
    public void initialize(ServiceBeanContext context) throws Exception {
        super.initialize(context);
        clusterManager = new CloudFederationClusterManager();
        List<CloudPlatformManager> clouds = (List<CloudPlatformManager>) getConfiguration().getEntry(
                CONFIG_COMPONENT, "cloudPlatformManagers", List.class);
        if (logger.isLoggable(Level.INFO)) {                // TODO: switch to CONFIG level instead
            for (CloudPlatformManager cloud : clouds)
                logger.info(String.format("Adding Cloud Platform %s", cloud.getName()));
        }
        ((CloudFederationClusterManager) clusterManager).setClouds(clouds);
    }

    /**
     * Get the component name to use for accessing the services configuration properties
     *
     * @return The component name
     */
    public static String getConfigComponent() {
        return configComponent;
    }

    public void startCluster(String clusterName, List clusterTopology) throws ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void stopCluster(String clusterName) throws ClusterException, RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Set findClusters() throws ClusterException, RemoteException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Cluster cluster(String name) throws ClusterException, RemoteException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void resizeCluster(String clusterName, List clusterTopology) throws ClusterNotFoundException, ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
