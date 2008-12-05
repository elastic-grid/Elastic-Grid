/**
 * Elastic Grid
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
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
import com.elasticgrid.model.NodeProfile;
import com.elasticgrid.model.ec2.EC2Node;
import com.elasticgrid.model.ec2.impl.EC2ClusterImpl;
import com.elasticgrid.model.ec2.impl.EC2NodeImpl;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Mock for {@link ClusterManager}.
 */
public class ClusterManagerMock implements ClusterManager {
    private Map<String, Cluster> clusters = new HashMap<String, Cluster>();

    public void startCluster(String clusterName) throws ClusterException, RemoteException, ExecutionException, TimeoutException, InterruptedException {
        startCluster(clusterName, 1);
    }

    public void startCluster(String clusterName, int size) throws ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
        startCluster(clusterName, size, 0);
    }

    public void startCluster(final String clusterName, int numberOfMonitors, int numberOfAgents) throws ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
        Cluster<EC2Node> cluster = new EC2ClusterImpl();
        cluster.name(clusterName);
        for (int i = 0; i < numberOfMonitors; i++) {
            EC2NodeImpl node = new EC2NodeImpl();
            node.instanceID(clusterName + '-' + i);
            node.profile(NodeProfile.MONITOR);
            try {
                node.address(InetAddress.getLocalHost());
            } catch (UnknownHostException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            cluster.getNodes().add(node);
        }
        for (int i = 0; i < numberOfAgents; i++) {
            EC2NodeImpl node = new EC2NodeImpl();
            node.instanceID(clusterName + '-' + i);
            node.profile(NodeProfile.AGENT);
            try {
                node.address(InetAddress.getLocalHost());
            } catch (UnknownHostException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            cluster.getNodes().add(node);
        }
        clusters.put(clusterName, cluster);
    }

    public void stopCluster(String clusterName) throws ClusterException, RemoteException {
        clusters.remove(clusterName);
    }

    public List<Cluster> findClusters() throws ClusterException, RemoteException {
        return new ArrayList<Cluster>(clusters.values());
    }

    public Cluster cluster(String name) throws ClusterException, RemoteException {
        return clusters.get(name);
    }

    public void resizeCluster(String clusterName, int newSize) throws ClusterNotFoundException, ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
        resizeCluster(clusterName, newSize, 0);
    }

    public void resizeCluster(String clusterName, int numberOfMonitors, int numberOfAgents) throws ClusterNotFoundException, ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
        Cluster cluster = clusters.get(clusterName);
        cluster.getNodes().clear();
        for (int i = 0; i < numberOfMonitors; i++) {
            EC2NodeImpl node = new EC2NodeImpl();
            node.instanceID(clusterName + '-' + i);
            node.profile(NodeProfile.MONITOR);
            try {
                node.address(InetAddress.getLocalHost());
            } catch (UnknownHostException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            cluster.getNodes().add(node);
        }
        for (int i = 0; i < numberOfAgents; i++) {
            EC2NodeImpl node = new EC2NodeImpl();
            node.instanceID(clusterName + '-' + i);
            node.profile(NodeProfile.AGENT);
            try {
                node.address(InetAddress.getLocalHost());
            } catch (UnknownHostException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            cluster.getNodes().add(node);
        }
    }
}
