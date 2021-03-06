/**
 * Elastic Grid
 * Copyright (C) 2008-2010 Elastic Grid, LLC.
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
package com.elasticgrid.platforms.ec2.discovery

import com.elasticgrid.cluster.discovery.ClusterChangedEvent
import com.elasticgrid.model.Application
import com.elasticgrid.model.Cluster
import com.elasticgrid.model.ClusterException
import com.elasticgrid.model.ClusterMonitorNotFoundException
import com.elasticgrid.model.ClusterNotFoundException
import com.elasticgrid.model.Discovery
import com.elasticgrid.model.NodeProfile
import com.elasticgrid.model.ec2.EC2Node
import com.elasticgrid.model.ec2.EC2NodeType
import com.elasticgrid.model.ec2.impl.EC2ClusterImpl
import com.elasticgrid.model.ec2.impl.EC2NodeImpl
import com.elasticgrid.platforms.ec2.discovery.EC2ClusterLocator
import com.elasticgrid.utils.logging.Log
import com.xerox.amazonws.ec2.EC2Exception
import com.xerox.amazonws.ec2.InstanceType
import com.xerox.amazonws.ec2.Jec2
import com.xerox.amazonws.ec2.ReservationDescription

/**
 * {@ClusterLocator} based on EC2 Security Groups, as described on Elastic Grid Blog post:
 * http://blog.elastic-grid.com/2008/06/30/how-to-do-some-service-discovery-on-amazon-ec2/
 */
class EC2SecurityGroupsClusterLocator extends EC2ClusterLocator {
  def Jec2 ec2
  def Map<String, Cluster> oldClusterDefinitions = new HashMap<String, Cluster>()

  /**
   * @{inheritDoc}
   * If the EC2 network can't be reached (offline mode or networking issues), then a empty list is returned.
   */
  public Set<String> findClusters() {
    Log.fine "EC2: Searching for clusters running on EC2..."
    List<ReservationDescription> reservations
    try {
      reservations = ec2.describeInstances(Collections.emptyList())
    } catch (EC2Exception e) {
      Log.warn "EC2: EC2 is not reachable. Ignoring EC2 clusters."
      return [] as Set<String>
    }
    // extract cluster names
    def Set<String> clusters = new HashSet<String>()
    reservations.each {ReservationDescription reservation ->
      reservation.groups.each {String group ->
        if (group.startsWith("elastic-grid-cluster-")) {
          if (reservation.instances.any { it.isRunning() })
            clusters << group.substring("elastic-grid-cluster-".length(), group.length())
        }
      }
    }
    Log.fine "EC2: Found clusters $clusters"
    return clusters
  }

  public Set<EC2Node> findNodes(String clusterName) throws ClusterNotFoundException, ClusterException {
    // retrieve the list of instances running
    Log.fine "EC2: Searching for Elastic Grid nodes in cluster '$clusterName'..."
    List<ReservationDescription> reservations
    try {
      reservations = ec2.describeInstances(Collections.emptyList())
    } catch (EC2Exception e) {
      Log.warn "EC2: EC2 is not reachable. Ignoring EC2 clusters."
      return Collections.emptySet()
    }
    // filter nodes which are not part of the cluster
    def clusterReservation = reservations.findAll {ReservationDescription reservation ->
      reservation.groups.contains "elastic-grid-cluster-$clusterName" as String
    }
    // build of list of all the instances
    def Set<EC2Node> nodes = clusterReservation.collect { ReservationDescription reservation ->
      reservation.instances.findAll { it.isRunning() }.collect { ReservationDescription.Instance instance ->
        boolean hasMonitor = reservation.groups.contains(Discovery.MONITOR.groupName)
        boolean hasAgent = reservation.groups.contains(Discovery.AGENT.groupName)
        def nodeType
        switch (instance.instanceType) {
          case InstanceType.DEFAULT:
            nodeType = EC2NodeType.SMALL
            break;
          case InstanceType.LARGE:
            nodeType = EC2NodeType.LARGE
            break;
          case InstanceType.XLARGE:
            nodeType = EC2NodeType.EXTRA_LARGE
            break;
          case InstanceType.MEDIUM_HCPU:
            nodeType = EC2NodeType.MEDIUM_HIGH_CPU
            break;
          case InstanceType.XLARGE_HCPU:
            nodeType = EC2NodeType.EXTRA_LARGE_HIGH_CPU
            break;
        }
        def profile = null
        if (!hasAgent && !hasMonitor) {
          Log.warn "EC2: Instance ${instance.instanceId} has no Elastic Grid profile!"
          return
        } else if (hasAgent && hasMonitor) {
          profile = NodeProfile.MONITOR_AND_AGENT
        } else if (hasMonitor) {
          profile = NodeProfile.MONITOR
        } else if (hasAgent) {
          profile = NodeProfile.AGENT
        }
        return new EC2NodeImpl(profile, nodeType).instanceID(instance.instanceId).address(InetAddress.getByName(instance.dnsName))
      }
    }.flatten() as Set<EC2Node>;
    Log.fine "EC2: Found ${nodes.size()} nodes in cluster '$clusterName'..."
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
    Log.fine "EC2: Searching for monitor node in cluster '$clusterName'..."
    def Collection<EC2Node> nodes = findNodes(clusterName)
    def found = false
    def node = nodes.find { it.profile.isMonitor() }
    if (node)
      return node as EC2Node
    else
      throw new ClusterMonitorNotFoundException(clusterName)
  }

  /** TODO: code this method! */
  public Set<Application> findApplications(String clusterName) throws ClusterException {
    return new HashSet<Application>();
  }

  public void setEc2(Jec2 ec2) {
    this.ec2 = ec2;
  }
}