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

import com.elasticgrid.model.Cluster;
import com.elasticgrid.model.ClusterException;
import com.elasticgrid.model.ClusterNotFoundException;
import com.elasticgrid.model.NodeProfileInfo;
import com.elasticgrid.cluster.spi.CloudPlatformManager;
import org.rioproject.core.jsb.ServiceBeanContext;
import org.rioproject.jsb.ServiceBeanAdapter;
import org.rioproject.associations.Association;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;
import net.jini.core.lookup.ServiceItem;

/**
 * JSB exposing the {@link ClusterManager}.
 *
 * @author Jerome Bernard
 */
public class ClusterManagerJSB extends ServiceBeanAdapter implements ClusterManager<Cluster> {
    private static ClusterManager<Cluster> clusterManager;
    private static final Logger logger = Logger.getLogger("com.elasticgrid.cluster");

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(ServiceBeanContext context) throws Exception {
        super.initialize(context);
        clusterManager = new CloudFederationClusterManager();
    }

    @Override
    public void advertise() throws IOException {
        super.advertise();
        logger.info("Advertised Cluster Manager");
    }

    public void startCluster(String clusterName, List<NodeProfileInfo> clusterTopology)
            throws ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
        clusterManager.startCluster(clusterName, clusterTopology);
    }

    public void stopCluster(String clusterName) throws ClusterException, RemoteException {
        clusterManager.stopCluster(clusterName);
    }

    public Set<Cluster> findClusters() throws ClusterException, RemoteException {
        return clusterManager.findClusters();
    }

    public Cluster cluster(String name) throws ClusterException, RemoteException {
        return clusterManager.cluster(name);
    }

    public void resizeCluster(String clusterName, List<NodeProfileInfo> clusterTopology)
            throws ClusterNotFoundException, ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
        clusterManager.resizeCluster(clusterName, clusterTopology);
    }

    @SuppressWarnings("unchecked")
    public void setCloudPlatforms(Association<CloudPlatformManager> association) throws RemoteException {
        List<CloudPlatformManager<Cluster>> clouds = new ArrayList<CloudPlatformManager<Cluster>>(association.getServiceCount());
        for (ServiceItem serviceItem : association.getServiceItems()) {
            CloudPlatformManager<Cluster> cloud = (CloudPlatformManager<Cluster>) serviceItem.service;
            if ("Private LAN".equals(cloud.getName()))
                clouds.add(0, cloud);       // first one
            else if ("Amazon EC2".equals(cloud.getName()))
                clouds.add(cloud);          // last one
            else
                logger.warning(String.format("Can't cope with %s platform yet", cloud.getName()));
        }
        ((CloudFederationClusterManager<Cluster>) clusterManager).setClouds(clouds);
    }

}
