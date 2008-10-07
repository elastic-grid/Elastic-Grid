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

package com.elasticgrid.amazon.ec2;

import com.elasticgrid.cluster.discovery.ClusterLocator;
import com.elasticgrid.cluster.ClusterManager;
import com.elasticgrid.cluster.NodeInstantiator;
import com.elasticgrid.amazon.ec2.InstanceType;
import com.elasticgrid.model.Cluster;
import com.elasticgrid.model.ClusterAlreadyRunningException;
import com.elasticgrid.model.ClusterException;
import com.elasticgrid.model.NodeProfile;
import com.elasticgrid.model.ec2.EC2Cluster;
import com.elasticgrid.model.ec2.EC2Node;
import com.elasticgrid.model.ec2.impl.EC2ClusterImpl;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import static java.lang.String.format;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service("clusterManager")
public class EC2ClusterManager implements ClusterManager<EC2Cluster> {
    private NodeInstantiator nodeInstantiator;
    private ClusterLocator clusterLocator;
    private String keyName;
    private String awsAccessID, awsSecretKey;
    private boolean awsSecured = true;
    private String ami32, ami64;
    private ExecutorService executor = Executors.newFixedThreadPool(5);
    private static final Logger logger = Logger.getLogger(EC2ClusterManager.class.getName());

    public void startCluster(String clusterName) throws ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
        startCluster(clusterName, 1);
    }

    public void startCluster(String clusterName, int size) throws ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
        logger.log(Level.INFO, "Starting cluster ''{0}'' with {1} node(s)...", new Object[] { clusterName, size });
        // ensure the cluster is not already running
        Cluster cluster = cluster(clusterName);
        if (cluster != null && cluster.isRunning()) {
            throw new ClusterAlreadyRunningException(cluster);
        }
        // todo: allow clients to specify which kind of EC2 instances to start
        InstanceType instanceType = InstanceType.SMALL;
        String ami = null;
        switch (instanceType) {
            case SMALL:
                ami = ami32;
                break;
            case MEDIUM_HIGH_CPU:
                ami = ami32;
                break;
            case LARGE:
                ami = ami64;
                break;
            case EXTRA_LARGE:
                ami = ami64;
                break;
            case EXTRA_LARGE_HIGH_CPU:
                ami = ami64;
                break;
            default:
                throw new IllegalArgumentException(format("Unexpected Amazon EC2 instance type '%s'", instanceType.getName()));
        }
        List<Future<List<String>>> futures = new ArrayList<Future<List<String>>>(size);
        for (int i = 0; i < size; i++) {
            // first first two nodes are {@link NodeProfile.MONITOR}s
            // and all the other ones are {@link NodeProfile.AGENT}s
            NodeProfile profile = i < 2 ? NodeProfile.MONITOR : NodeProfile.AGENT;
            // start the node
            futures.add(executor.submit(new StartInstanceTask(nodeInstantiator, clusterName, profile, instanceType, ami,
                            awsAccessID, awsSecretKey, awsSecured)));
        }
        // wait for the threads to finish
        for (Future<List<String>> future : futures) {
            future.get(5 * 60, TimeUnit.SECONDS);
        }
    }

    public void stopCluster(String clusterName) throws ClusterException, RemoteException {
        logger.log(Level.INFO, "Stopping cluster ''{0}''", new Object[] { clusterName });
        // locate all nodes in the cluster
        List<EC2Node> nodes = clusterLocator.findNodes(clusterName);
        // stop each node one by one
        for (EC2Node node : nodes) {
            nodeInstantiator.shutdownInstance(node.getInstanceID());
        }
    }

    public List<Cluster> findClusters() throws ClusterException, RemoteException {
        List<String> clustersNames = clusterLocator.findClusters();
        List<Cluster> clusters = new ArrayList<Cluster>(clustersNames.size());
        for (String cluster : clustersNames) {
            clusters.add(cluster(cluster));
        }
        return clusters;
    }

    public EC2Cluster cluster(String name) throws RemoteException, ClusterException {
        EC2Cluster cluster = new EC2ClusterImpl();
        List<EC2Node> nodes = clusterLocator.findNodes(name);
        if (nodes == null)
            return cluster;
        else
            return (EC2Cluster) cluster.name(name).addNodes(nodes);
    }

    public void resizeCluster(String clusterName, int newSize) throws ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
        EC2Cluster cluster = cluster(clusterName);
        Set<EC2Node> nodes = cluster.getNodes();
        if (nodes.size() == newSize) {
            // same size -- do nothing
            logger.log(Level.WARNING, "Cluster '{0}' is already made of {1} instances", new Object[] { clusterName, newSize });
        } else if (nodes.size() < newSize) {
            // increase cluster size
            int toStart = newSize - nodes.size();
            logger.log(Level.INFO,
                    "Scaling cluster ''{0}'' with {1} additional node(s)...",
                    new Object[] { clusterName, toStart });
            int numberOfMonitors = 0;
            for (EC2Node node : nodes) {
                switch (node.getProfile()) {
                    case MONITOR:
                        numberOfMonitors++;
                        break;
                    case AGENT:
                        break;
                }
            }
            // todo: allow clients to specify which kind of EC2 instances to start
            InstanceType instanceType = InstanceType.SMALL;
            String ami;
            switch (instanceType) {
                case SMALL:
                case MEDIUM_HIGH_CPU:
                    ami = ami32;
                    break;
                case LARGE:
                case EXTRA_LARGE:
                case EXTRA_LARGE_HIGH_CPU:
                    ami = ami64;
                    break;
                default:
                    throw new IllegalArgumentException(format("Unexpected Amazon EC2 instance type '%s'", instanceType.getName()));
            }
            List<Future<List<String>>> futures = new ArrayList<Future<List<String>>>(toStart);
            // if there is less than two monitors, start some new ones
            while (numberOfMonitors < 2) {
                futures.add(executor.submit(new StartInstanceTask(nodeInstantiator, clusterName, NodeProfile.MONITOR, instanceType, ami,
                        awsAccessID, awsSecretKey, awsSecured)));
                numberOfMonitors++;
                toStart--;
            }
            // otherwise start some new agents
            while (toStart-- > 0) {
                futures.add(executor.submit(new StartInstanceTask(nodeInstantiator, clusterName, NodeProfile.AGENT, instanceType, ami,
                        awsAccessID, awsSecretKey, awsSecured)));
            }
            // wait for the threads to finish
            for (Future<List<String>> future : futures) {
                future.get(5 * 60, TimeUnit.SECONDS);
            }
        } else {
            // decrease cluster size
            if (newSize < 1)
                throw new IllegalArgumentException("newSize is invalid");
            logger.log(Level.INFO,
                    "Decreasing cluster ''{0}'' by {1} node(s)...",
                    new Object[] { clusterName, nodes.size() - newSize });
            if (newSize == 1) {
                // locate a monitor and keep it only
                boolean found = false;
                for (EC2Node node : nodes) {
                    if (NodeProfile.MONITOR.equals(node.getProfile())) {
                        if (found)
                            nodeInstantiator.shutdownInstance(node.getInstanceID());
                        else
                            found = true;
                    } else {
                        nodeInstantiator.shutdownInstance(node.getInstanceID());
                    }
                }
            } else {
                // kill some agents until we try to reach the count
                int toKill = nodes.size() - newSize;
                for (EC2Node node : nodes) {
                    if (NodeProfile.AGENT.equals(node.getProfile())) {
                        if (toKill-- > 0)
                            nodeInstantiator.shutdownInstance(node.getInstanceID());
                    }
                }
                // check if we need to kill some monitors too
                if (toKill > 0) {
                    for (EC2Node node : nodes) {
                        if (NodeProfile.MONITOR.equals(node.getProfile())) {
                            if (toKill-- > 0)
                                nodeInstantiator.shutdownInstance(node.getInstanceID());
                        }
                    }
                }
            }
        }
    }

    @Autowired(required = true)
    public void setNodeInstantiator(NodeInstantiator nodeInstantiator) {
        this.nodeInstantiator = nodeInstantiator;
    }

    @Autowired(required = true)
    public void setClusterLocator(ClusterLocator clusterLocator) {
        this.clusterLocator = clusterLocator;
    }

    @Required
    public void setAwsAccessID(String awsAccessID) {
        this.awsAccessID = awsAccessID;
    }

    @Required
    public void setAwsSecretKey(String awsSecretKey) {
        this.awsSecretKey = awsSecretKey;
    }

    @Required
    public void setAwsSecured(boolean awsSecured) {
        this.awsSecured = awsSecured;
    }

    @Required
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    @Required
    public void setAmi32(String ami32) {
        this.ami32 = ami32;
    }

    @Required
    public void setAmi64(String ami64) {
        this.ami64 = ami64;
    }

    class StartInstanceTask implements Callable<List<String>> {
        private NodeInstantiator nodeInstantiator;
        private String clusterName;
        private NodeProfile profile;
        private InstanceType instanceType;
        private String ami;
        private String userData;

        public StartInstanceTask(NodeInstantiator nodeInstantiator, String clusterName, NodeProfile profile, InstanceType instanceType,
                                 String ami, String awsAccessId, String awsSecretKey, boolean awsSecured) {
            this.nodeInstantiator = nodeInstantiator;
            this.clusterName = clusterName;
            this.profile = profile;
            this.instanceType = instanceType;
            this.ami = ami;
            this.userData = String.format(
                    "CLUSTER_NAME=%s,AWS_ACCESS_ID=%s,AWS_SECRET_KEY=%s,AWS_SQS_SECURED=%b",
                    clusterName, awsAccessId, awsSecretKey, awsSecured);
        }

        public List<String> call() throws RemoteException {
            // ensure the monitor group exists
            if (!nodeInstantiator.getGroupsNames().contains(NodeProfile.MONITOR.toString())) {
                nodeInstantiator.createProfileGroup(NodeProfile.MONITOR);
            }
            // ensure the monitor group exists
            if (!nodeInstantiator.getGroupsNames().contains(NodeProfile.AGENT.toString())) {
                nodeInstantiator.createProfileGroup(NodeProfile.AGENT);
            }
            // ensure the cluster name group exists
            if (!nodeInstantiator.getGroupsNames().contains("elastic-grid-cluster-" + clusterName)) {
                nodeInstantiator.createClusterGroup(clusterName);
            }
            // start the agent node
            List<String> groups = Arrays.asList("elastic-grid-cluster-" + clusterName, profile.toString(), "elastic-grid");
            logger.log(Level.INFO, "Starting 1 Amazon EC2 instance from AMI {0} using groups {1} and user data {2}...",
                                       new Object[] { ami, groups.toString(), userData });
            return nodeInstantiator.startInstances(ami, 1, 1, groups, userData, keyName, true, instanceType);
        }
    }
}
