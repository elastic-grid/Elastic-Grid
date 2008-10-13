/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This file is part of Elastic Grid.
 * 
 * Elastic Grid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * 
 * Elastic Grid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Elastic Grid.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elasticgrid.cluster;

import com.elasticgrid.model.Cluster;
import com.elasticgrid.model.ClusterException;
import com.elasticgrid.model.ClusterNotFoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface ClusterManager<G extends Cluster> {
    /**
     * Start a cluster with a specified name.
     * Same as calling {@link #startCluster(String, int)} with a <tt>size</tt> of <tt>1</tt>.
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
     * This method will both start monitors and agents as appropriate depending on the <tt>size</tt> parameter.
     * @param clusterName the name of the cluster to start
     * @param size the number of nodes to start for this cluster
     * @throws InterruptedException if the cluster was not started before timeout
     * @throws RemoteException if there is a network failure
     * @throws ClusterException if there is a cluster failure
     * @throws ExecutionException
     * @throws TimeoutException
     * @see #startCluster(String)
     * @see #startCluster(String,int,int)
     */
    void startCluster(String clusterName, int size)
            throws ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException;

    /**
     * Start a cluster with a specified name and a specified number of instances.
     * @param clusterName the name of the cluster to start
     * @param numberOfMonitors the number of monitor nodes to start for this cluster
     * @param numberOfAgents the number of agents nodes to start for this cluster
     * @throws InterruptedException if the cluster was not started before timeout
     * @throws RemoteException if there is a network failure
     * @throws ClusterException if there is a cluster failure
     * @throws ExecutionException
     * @throws TimeoutException
     * @see #startCluster(String)
     * @see #startCluster(String,int)
     */
    void startCluster(String clusterName, int numberOfMonitors, int numberOfAgents)
            throws ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException;

    void stopCluster(String clusterName) throws ClusterException, RemoteException;

    /**
     * Retreive all clusters details.
     * @return the clusters details
     * @throws ClusterException if there is a cluster failure
     * @throws RemoteException if there is a network failure
     */
    List<G> findClusters() throws ClusterException, RemoteException;

    /**
     * Retrieve cluster details.
     * @param name the name of the cluster
     * @return the cluster or null if the cluster is not found
     * @throws ClusterException if there is a cluster failure
     * @throws RemoteException if there is a network failure
     */
    G cluster(String name) throws ClusterException, RemoteException;

    /**
     * Resize a cluster.
     * This method will both start monitors and agents as appropriate depending on the <tt>size</tt> parameter.
     * @param clusterName the name of the cluster to resize
     * @param newSize the new size of the cluster
     * @throws ClusterNotFoundException if the cluster can't be found
     * @throws ClusterException if there is a cluster failure
     * @throws InterruptedException if the cluster was not started before timeout
     * @throws RemoteException if there is a network failure
     * @throws ExecutionException
     * @throws TimeoutException
     */
    void resizeCluster(String clusterName, int newSize)
            throws ClusterNotFoundException, ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException;

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
    void resizeCluster(String clusterName, int numberOfMonitors, int numberOfAgents)
            throws ClusterNotFoundException, ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException;
}
