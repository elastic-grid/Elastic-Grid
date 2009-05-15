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

package com.elasticgrid.cluster.spi;

import com.elasticgrid.model.NodeProfileInfo;
import com.elasticgrid.model.Cluster;
import com.elasticgrid.model.ClusterException;
import com.elasticgrid.model.ClusterNotFoundException;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface CloudPlatformManager<C extends Cluster> {

    String getName();

    /**
     * Start a cluster with a specified name and a specified number of instances.
     * @param clusterName the name of the cluster to start
     * @param clusterTopology information about the cluster topology
     * @throws InterruptedException if the cluster was not started before timeout
     * @throws java.rmi.RemoteException if there is a network failure
     * @throws com.elasticgrid.model.ClusterException if there is a cluster failure
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     */
    void startCluster(String clusterName, List<NodeProfileInfo> clusterTopology)
            throws ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException;

    void stopCluster(String clusterName) throws ClusterException, RemoteException;

    /**
     * Retreive all clusters details.
     * @return the clusters details
     * @throws com.elasticgrid.model.ClusterException if there is a cluster failure
     * @throws java.rmi.RemoteException if there is a network failure
     */
    Collection<C> findClusters() throws ClusterException, RemoteException;

    /**
     * Retrieve cluster details.
     * @param name the name of the cluster
     * @return the cluster or null if the cluster is not found
     * @throws com.elasticgrid.model.ClusterException if there is a cluster failure
     * @throws java.rmi.RemoteException if there is a network failure
     */
    C cluster(String name) throws ClusterException, RemoteException;

    /**
     * Resize a cluster.
     * @param clusterName the name of the cluster to resize
     * @param clusterTopology information about the cluster topology
     * @throws com.elasticgrid.model.ClusterNotFoundException if the cluster can't be found
     * @throws com.elasticgrid.model.ClusterException if there is a cluster failure
     * @throws InterruptedException if the cluster was not started before timeout
     * @throws java.rmi.RemoteException if there is a network failure
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     */
    void resizeCluster(String clusterName, List<NodeProfileInfo> clusterTopology)
            throws ClusterNotFoundException, ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException;

}
