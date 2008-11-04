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

package com.elasticgrid.platforms.lan;

import com.elasticgrid.cluster.ClusterManager;
import com.elasticgrid.cluster.discovery.ClusterLocator;
import com.elasticgrid.model.Cluster;
import com.elasticgrid.model.ClusterException;
import com.elasticgrid.model.lan.LANCluster;
import com.elasticgrid.model.lan.LANNode;
import com.elasticgrid.model.lan.impl.LANClusterImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

@Service("clusterManager")
public class LANClusterManager implements ClusterManager<Cluster> {
    private ClusterLocator<LANNode> clusterLocator;
    private String keyName;
    private static final Logger logger = Logger.getLogger(LANClusterManager.class.getName());

    public void startCluster(String clusterName) throws ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
        throw new UnsupportedOperationException("There is no way to start a new cluster on a private LAN.");
    }

    public void startCluster(String clusterName, int size) throws ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
        throw new UnsupportedOperationException("There is no way to start a new cluster on a private LAN.");
    }

    public void startCluster(String clusterName, int numberOfMonitors, int numberOfAgents) throws ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
        throw new UnsupportedOperationException("There is no way to start a new cluster on a private LAN.");
    }

    public void stopCluster(String clusterName) throws ClusterException, RemoteException {
        throw new UnsupportedOperationException("There is no way to stop a cluster on a private LAN.");
    }

    public List<Cluster> findClusters() throws ClusterException, RemoteException {
        List<String> clustersNames = clusterLocator.findClusters();
        List<Cluster> clusters = new ArrayList<Cluster>(clustersNames.size());
        for (String cluster : clustersNames) {
            clusters.add(cluster(cluster));
        }
        return clusters;
    }

    public LANCluster cluster(String name) throws RemoteException, ClusterException {
        LANCluster cluster = new LANClusterImpl();
        List<LANNode> nodes = clusterLocator.findNodes(name);
        if (nodes == null)
            return cluster;
        else
            return (LANCluster) cluster.name(name).addNodes(nodes);
    }

    public void resizeCluster(String clusterName, int newSize) throws ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
        throw new UnsupportedOperationException("There is no way to resize a cluster on a private LAN.");
    }

    public void resizeCluster(String clusterName, int numberOfMonitors, int numberOfAgents) throws ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
        throw new UnsupportedOperationException("There is no way to resize a cluster on a private LAN.");
    }

    @Autowired(required = true)
    public void setClusterLocator(ClusterLocator<LANNode> clusterLocator) {
        this.clusterLocator = clusterLocator;
    }

}