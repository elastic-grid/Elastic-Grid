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

package com.elasticgrid.platforms.rackspace;

import com.elasticgrid.cluster.discovery.ClusterLocator;
import com.elasticgrid.cluster.spi.AbstractCloudPlatformManager;
import com.elasticgrid.cluster.spi.CloudPlatformManager;
import com.elasticgrid.model.ClusterException;
import com.elasticgrid.model.NodeProfileInfo;
import com.elasticgrid.model.rackspace.CloudServersCluster;
import com.elasticgrid.model.rackspace.CloudServersNode;
import com.elasticgrid.model.rackspace.impl.CloudServersClusterImpl;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CloudServersCloudPlatformManager extends AbstractCloudPlatformManager<CloudServersCluster> implements CloudPlatformManager<CloudServersCluster> {
    private CloudServersNodeInstantiator nodeInstantiator;
    private ClusterLocator<CloudServersNode> clusterLocator;

    private String overridesBucket;
    private String username, apiKey;
    private String image;
    private ExecutorService executor = Executors.newFixedThreadPool(5);
    private static final Logger logger = Logger.getLogger(CloudServersCloudPlatformManager.class.getName());

    public String getName() {
        return "Rackspace Cloud Servers";
    }

    public void startCluster(String clusterName, List<NodeProfileInfo> clusterTopology) throws ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
        /*
        // ensure the cluster name group exists
        String securityGroupNameForCluster = "elastic-grid-cluster-" + clusterName;
        if (!nodeInstantiator.getGroupsNames().contains(securityGroupNameForCluster)) {
            nodeInstantiator.createSecurityGroup(securityGroupNameForCluster);
        }

        // ensure the cluster is not already running
        Cluster cluster = cluster(clusterName);
        if (cluster != null && cluster.isRunning()) {
            throw new ClusterAlreadyRunningException(cluster);
        }

        List<Future<List<String>>> futures = new LinkedList<Future<List<String>>>();
        for (NodeProfileInfo nodeProfileInfo : clusterTopology) {
            for (int i = 0; i < nodeProfileInfo.getNumber(); i++) {
                String ami;
                switch ((EC2NodeType) nodeProfileInfo.getNodeType()) {
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
                        throw new IllegalArgumentException(format("Unexpected Amazon EC2 instance type '%s'",
                                nodeProfileInfo.getNodeType().getName()));
                }
                logger.log(Level.INFO, "Starting cluster ["+clusterName+"], " +
                                       "type ["+nodeProfileInfo.getNodeType().getName()+"], " +
                                       "using AMI ["+ami+"]");
                String override = null;
                if (nodeProfileInfo.hasOverride())
                    override = "s3://" + overridesBucket;
                futures.add(executor.submit(new StartInstanceTask(nodeInstantiator, clusterName,
                        nodeProfileInfo.getNodeProfile(), (EC2NodeType) nodeProfileInfo.getNodeType(),
                        override, ami, awsAccessID, awsSecretKey, awsSecured)));
            }
        }

        // wait for the threads to finish
        for (Future<List<String>> future : futures) {
            future.get(5 * 60, TimeUnit.SECONDS);
        }
        */
    }

    public void stopCluster(String clusterName) throws ClusterException, RemoteException {
        logger.log(Level.INFO, "Stopping cluster ''{0}''", new Object[] { clusterName });
        // locate all nodes in the cluster
        Collection<CloudServersNode> nodes = clusterLocator.findNodes(clusterName);
        // stop each node one by one
        for (CloudServersNode node : nodes) {
            nodeInstantiator.shutdownInstance(node.getServerID());
        }
    }

    public Collection<CloudServersCluster> findClusters() throws ClusterException, RemoteException {
        Collection<String> clustersNames = clusterLocator.findClusters();
        List<CloudServersCluster> clusters = new ArrayList<CloudServersCluster>(clustersNames.size());
        for (String cluster : clustersNames) {
            clusters.add(cluster(cluster));
        }
        return clusters;
    }

    public CloudServersCluster cluster(String name) throws RemoteException, ClusterException {
        CloudServersCluster cluster = new CloudServersClusterImpl();
        Set<CloudServersNode> nodes = clusterLocator.findNodes(name);
        if (nodes == null)
            return (CloudServersCluster) cluster.name(name);
        else
            return (CloudServersCluster) cluster.name(name).addNodes(nodes);
    }

    public void resizeCluster(String clusterName, List<NodeProfileInfo> clusterTopology) throws ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
        /*
        // inspect the current cluster in order to figure out its topology
        CloudServersCluster cluster = cluster(clusterName);

        if (cluster.getNodes().isEmpty())
            throw new ClusterNotFoundException(clusterName);

        Set<CloudServersNode> monitors = cluster.getMonitorNodes();
        Set<CloudServersNode> agents = cluster.getAgentNodes();
        // figure out which MONITORs are actually MONITOR_AND_AGENT and update the MONITOR set
        Set<CloudServersNode> monitorsAndAgents = new HashSet<CloudServersNode>();
        Iterator<CloudServersNode> monitorsIterator = monitors.iterator();
        while (monitorsIterator.hasNext()) {
            CloudServersNode csNode =  monitorsIterator.next();
            if (csNode.getProfile().isAgent()) {
                monitorsAndAgents.add(csNode);
                monitorsIterator.remove();
            }
        }

        int numberOfMonitors = monitors.size();
        int numberOfMonitorsAndAgents = monitorsAndAgents.size();
        int numberOfAgents = agents.size();

        logger.log(Level.FINEST, "Cluster {0} made of {1} monitor(s), {2} agent(s) and {3} monitor(s) and agent(s)",
                new Object[] { clusterName, numberOfMonitors, numberOfAgents, numberOfMonitorsAndAgents });

        List<Future> futures = new LinkedList<Future>();
        for (NodeProfileInfo nodeProfileInfo : clusterTopology) {
            logger.log(Level.FINEST, "Cluster should be made of {0} node(s) of profile {1} and type {2}",
                    new Object[] { nodeProfileInfo.getNumber(), nodeProfileInfo.getNodeProfile(), nodeProfileInfo.getNodeType() });
            String ami;
            switch ((EC2NodeType) nodeProfileInfo.getNodeType()) {
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
                    throw new IllegalArgumentException(format("Unexpected Amazon EC2 instance type '%s'",
                            nodeProfileInfo.getNodeType().getName()));
            }
            int number = 0;
            Iterator<EC2Node> nodesIterator = null;
            switch (nodeProfileInfo.getNodeProfile()) {
                case MONITOR:
                    number = nodeProfileInfo.getNumber() - numberOfMonitors;
                    nodesIterator = monitors.iterator();
                    break;
                case MONITOR_AND_AGENT:
                    number = nodeProfileInfo.getNumber() - numberOfMonitorsAndAgents;
                    nodesIterator = monitorsAndAgents.iterator();
                    break;
                case AGENT:
                    number = nodeProfileInfo.getNumber() - numberOfAgents;
                    nodesIterator = agents.iterator();
                    break;
            }

            if (number > 0) {
                logger.log(Level.INFO, "Scaling cluster ''{0}'' with {1} additional node(s) of profile {1}...",
                        new Object[] { clusterName, number, nodeProfileInfo.getNodeProfile() });
                String override = null;
                if (nodeProfileInfo.hasOverride())
                    override = "s3://" + overridesBucket;
                for (int i = 0; i < number; i++) {
                    futures.add(executor.submit(new StartInstanceTask(nodeInstantiator, clusterName,
                            nodeProfileInfo.getNodeProfile(), (EC2NodeType) nodeProfileInfo.getNodeType(),
                            override, ami, awsAccessID, awsSecretKey, awsSecured)));
                }
            } else {
                logger.log(Level.INFO, "Decreasing cluster ''{0}'' by {1} node(s) of profile {2}...",
                        new Object[] { clusterName, Math.abs(number), nodeProfileInfo.getNodeProfile() });
                int numberToStop = Math.abs(number);
                while (numberToStop > 0 && nodesIterator.hasNext()) {
                    EC2Node node = nodesIterator.next();
                    futures.add(executor.submit(new StopInstanceTask(nodeInstantiator, node.getInstanceID())));
                }
            }
        }

        // wait for the threads to finish
        for (Future future : futures) {
            future.get(5 * 60, TimeUnit.SECONDS);
        }
        */
    }

    public void setNodeInstantiator(CloudServersNodeInstantiator nodeInstantiator) throws RemoteException {
        this.nodeInstantiator = nodeInstantiator;
        /*
        // ensure the Discovery.MONITOR group exists
        List<String> groupNames = nodeInstantiator.getGroupsNames();
        if (!groupNames.contains(Discovery.MONITOR.getGroupName())) {
            nodeInstantiator.createSecurityGroup(Discovery.MONITOR.getGroupName());
        }
        // ensure the Discovery.AGENT group exists
        if (!groupNames.contains(Discovery.AGENT.getGroupName())) {
            nodeInstantiator.createSecurityGroup(Discovery.AGENT.getGroupName());
        }
        */
    }

    public void setOverridesBucket(String overridesBucket) {
        this.overridesBucket = overridesBucket;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setClusterLocator(ClusterLocator clusterLocator) {
        this.clusterLocator = clusterLocator;
    }

}