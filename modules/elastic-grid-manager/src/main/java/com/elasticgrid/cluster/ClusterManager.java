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
import java.rmi.RemoteException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface ClusterManager<C extends Cluster> {
    /**
     * Start a cluster with a specified name.
     * This is the same as calling {@link #startCluster(String, int, int, int)} with only 1 "monitor and agent".
     * @param clusterName the name of the cluster to start
     * @throws InterruptedException if the cluster was not started before timeout
     * @throws ClusterException if there is a cluster failure
     * @throws ExecutionException
     * @throws TimeoutException
     * @throws RemoteException if there is a network failure
     * @see #startCluster (String, int)
     * @see #startCluster (String, int,int)
     */
    void startCluster(String clusterName)
            throws ClusterException, RemoteException, ExecutionException, TimeoutException, InterruptedException;

    /**
     * Start a cluster with a specified name and a specified number of instances.
     * @param clusterName the name of the cluster to start
     * @param numberOfMonitors the number of monitor nodes to start for this cluster
     * @param numberOfMonitorsAndAgents the number of "monitor + agent" nodes to start for this cluster
     * @param numberOfAgents the number of agents nodes to start for this cluster
     * @throws InterruptedException if the cluster was not started before timeout
     * @throws RemoteException if there is a network failure
     * @throws ClusterException if there is a cluster failure
     * @throws ExecutionException
     * @throws TimeoutException
     * @see #startCluster(String)
     */
    void startCluster(String clusterName, int numberOfMonitors, int numberOfMonitorsAndAgents, int numberOfAgents)
            throws ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException;

    void stopCluster(String clusterName) throws ClusterException, RemoteException;

    /**
     * Retreive all clusters details.
     * @return the clusters details
     * @throws ClusterException if there is a cluster failure
     * @throws RemoteException if there is a network failure
     */
    Set<C> findClusters() throws ClusterException, RemoteException;

    /**
     * Retrieve cluster details.
     * @param name the name of the cluster
     * @return the cluster or null if the cluster is not found
     * @throws ClusterException if there is a cluster failure
     * @throws RemoteException if there is a network failure
     */
    C cluster(String name) throws ClusterException, RemoteException;

    /**
     * Resize a cluster.
     * @param clusterName the name of the cluster to resize
     * @param numberOfMonitors the number of monitor nodes to reach for this cluster
     * @param numberOfAgents the number of agents nodes to reach for this cluster
     * @throws ClusterNotFoundException if the cluster can't be found
     * @throws ClusterException if there is a cluster failure
     * @throws InterruptedException if the cluster was not started before timeout
     * @throws RemoteException if there is a network failure
     * @throws ExecutionException
     * @throws TimeoutException
     */
    void resizeCluster(String clusterName, int numberOfMonitors, int numberOfMonitorsAndAgents, int numberOfAgents)
            throws ClusterNotFoundException, ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException;
}
