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

import com.elasticgrid.model.*;
import com.elasticgrid.model.ec2.EC2Node;
import com.elasticgrid.model.ec2.EC2NodeType;
import com.elasticgrid.model.ec2.impl.EC2ClusterImpl;
import com.elasticgrid.model.ec2.impl.EC2NodeImpl;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Mock for {@link ClusterManager}.
 */
public class ClusterManagerMock implements ClusterManager {
    private Map<String, Cluster> clusters = new HashMap<String, Cluster>();

    public void startCluster(final String clusterName, List clusterTopology)
            throws ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
        Cluster<EC2Node> cluster = new EC2ClusterImpl();
        cluster.name(clusterName);
        for (int i = 0; i < clusterTopology.size(); i++) {
            NodeProfileInfo nodeProfileInfo = (NodeProfileInfo) clusterTopology.get(i);
            EC2NodeImpl node = new EC2NodeImpl();
            node.instanceID(clusterName + Math.random());
            node.setProfile(nodeProfileInfo.getNodeProfile());
            node.setType((EC2NodeType) nodeProfileInfo.getNodeType());
            try {
                node.address(InetAddress.getLocalHost());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            cluster.getNodes().add(node);
        }
        clusters.put(clusterName, cluster);
    }

    public void stopCluster(String clusterName) throws ClusterException, RemoteException {
        clusters.remove(clusterName);
    }

    public Set<Cluster> findClusters() throws ClusterException, RemoteException {
        return new HashSet<Cluster>(clusters.values());
    }

    public Cluster cluster(String name) throws ClusterException, RemoteException {
        return clusters.get(name);
    }

    @SuppressWarnings("unchecked")
    public void resizeCluster(String clusterName, List clusterTopology)
            throws ClusterNotFoundException, ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
        Cluster<EC2Node> cluster = clusters.get(clusterName);
        cluster.getNodes().clear();
        for (int i = 0; i < clusterTopology.size(); i++) {
            NodeProfileInfo nodeProfileInfo = (NodeProfileInfo) clusterTopology.get(i);
            EC2NodeImpl node = new EC2NodeImpl();
            node.instanceID(clusterName + '-' + Math.random());
            node.setProfile(nodeProfileInfo.getNodeProfile());
            node.setType((EC2NodeType) nodeProfileInfo.getNodeType());
            try {
                node.address(InetAddress.getLocalHost());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            cluster.getNodes().add(node);
        }
    }
}
