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

package com.elasticgrid.platforms.ec2.discovery

import com.elasticgrid.cluster.discovery.ClusterChangedEvent
import com.elasticgrid.model.Cluster
import com.elasticgrid.model.ClusterException
import com.elasticgrid.model.ClusterMonitorNotFoundException
import com.elasticgrid.model.ClusterNotFoundException
import com.elasticgrid.model.NodeProfile
import com.elasticgrid.model.ec2.EC2Node
import com.elasticgrid.model.ec2.impl.EC2ClusterImpl
import com.elasticgrid.model.ec2.impl.EC2NodeImpl
import com.xerox.amazonws.ec2.EC2Exception
import com.xerox.amazonws.ec2.Jec2
import com.xerox.amazonws.ec2.ReservationDescription
import java.util.logging.Level
import java.util.logging.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * {@ClusterLocator} based on EC2 Security Groups, as described on Elastic Grid Blog post:
 * http://blog.elastic-grid.com/2008/06/30/how-to-do-some-service-discovery-on-amazon-ec2/
 */
@Service
class EC2SecurityGroupsClusterLocator extends EC2ClusterLocator {
    @Autowired(required = true)
    def Jec2 ec2

    def Map<String, Cluster> oldClusterDefinitions = new HashMap<String, Cluster>()
    public static final String EG_GROUP_MONITOR = "eg-monitor"
    public static final String EG_GROUP_AGENT = "eg-agent"
    private static final Logger logger = Logger.getLogger(EC2ClusterLocator.class.name)

    public List<String> findClusters() {
        logger.info "Searching for all clusters..."
        List<ReservationDescription> reservations
        try {
            reservations = ec2.describeInstances(Collections.emptyList())
        } catch (EC2Exception e) {
            throw new ClusterException("Can't locate clusters", e)
        }
        // extract cluster names
        def Set clusters = new HashSet()
        reservations.each { ReservationDescription reservation ->
            reservation.groups.each { String group ->
                if (group.startsWith("elastic-grid-cluster-")) {
                    if (reservation.instances.any { it.isRunning() })
                        clusters << group.substring("elastic-grid-cluster-".length(), group.length())
                }
            }
        }
        return clusters as List;
    }

    public List<EC2Node> findNodes(String clusterName) throws ClusterNotFoundException, ClusterException {
        // retrieve the list of instances running
        logger.log Level.INFO, "Searching for Elastic Grid nodes in cluster '$clusterName'..."
        List<ReservationDescription> reservations
        try {
            reservations = ec2.describeInstances(Collections.emptyList())
        } catch (EC2Exception e) {
            throw new ClusterException("Can't locate cluster $clusterName", e)
        }
        // filter nodes which are not part of the cluster
        def clusterReservation = reservations.findAll { ReservationDescription reservation ->
            reservation.groups.contains "elastic-grid-cluster-$clusterName" as String
        }
        // build of list of all the instances
        List nodes = clusterReservation.collect { ReservationDescription reservation ->
            reservation.instances.findAll { it.isRunning() }.collect { ReservationDescription.Instance instance ->
                boolean monitor = reservation.groups.contains(NodeProfile.MONITOR.toString())
                boolean agent = reservation.groups.contains(NodeProfile.AGENT.toString())
                def profile = null;
                if (!agent && !monitor) {
                    logger.log Level.WARNING, "Instance ${instance.instanceId} has no Elastic Grid profile!"
                    return
                } else if (agent && monitor) {
                    logger.log Level.WARNING,
                            "Instance ${instance.instanceId} is both a monitor and an agent. Using it as a monitor!"
                    profile = NodeProfile.MONITOR
                } else if (monitor) {
                    profile = NodeProfile.MONITOR
                } else if (agent) {
                    profile = NodeProfile.AGENT
                }
                return new EC2NodeImpl(profile)
                        .instanceID(instance.instanceId)
                        .address(InetAddress.getByName(instance.dnsName))
            }
        }.flatten()
        logger.log Level.INFO, "Found ${nodes.size()} nodes in cluster '$clusterName'..."
        // notify listeners of potential cluster topology changes
        Cluster<EC2Node> cluster = new EC2ClusterImpl(name: clusterName).addNodes(nodes)
        if (oldClusterDefinitions.containsKey(clusterName)) {
            Cluster old = oldClusterDefinitions.get(clusterName)
            if (!old.equals(cluster)) {
                oldClusterDefinitions.put(clusterName, cluster)
                fireClusterChangedEvent new ClusterChangedEvent(this, cluster)
            }
        } else {
            oldClusterDefinitions.put(clusterName, cluster)
            fireClusterChangedEvent new ClusterChangedEvent(this, cluster)
        }
        fireClusterChangedEvent new ClusterChangedEvent(this, cluster)
        return nodes
    }

    public EC2Node findMonitor(String clusterName) throws ClusterMonitorNotFoundException {
        logger.log Level.INFO, "Searching for monitor node in cluster '$clusterName'..."
        def List<EC2Node> nodes = findNodes(clusterName)
        def found = false
        def node = nodes.find { NodeProfile.MONITOR == it.profile}
        if (node)
            return node
        else
            throw new ClusterMonitorNotFoundException(clusterName)
    }

    @Autowired
    public void setEc2(Jec2 ec2) {
        this.ec2 = ec2;
    }
}