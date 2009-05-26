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

package com.elasticgrid.platforms.lan.discovery

import com.elasticgrid.cluster.discovery.ClusterLocator
import com.elasticgrid.model.Application
import com.elasticgrid.model.ClusterException
import com.elasticgrid.model.ClusterMonitorNotFoundException
import com.elasticgrid.model.ClusterNotFoundException
import com.elasticgrid.model.NodeProfile
import com.elasticgrid.model.internal.ApplicationImpl
import com.elasticgrid.model.lan.LANNode
import com.elasticgrid.model.lan.impl.LANNodeImpl
import com.elasticgrid.platforms.lan.discovery.AgentGroupFilter
import com.elasticgrid.platforms.lan.discovery.LANClusterLocator
import com.elasticgrid.platforms.lan.discovery.MonitorGroupFilter
import java.rmi.registry.LocateRegistry
import java.rmi.registry.Registry
import net.jini.config.ConfigurationProvider
import net.jini.core.lookup.ServiceItem
import net.jini.core.lookup.ServiceTemplate
import net.jini.discovery.LookupDiscoveryManager
import net.jini.lease.LeaseRenewalManager
import net.jini.lookup.LookupCache
import net.jini.lookup.ServiceDiscoveryManager
import net.jini.lookup.entry.Host
import org.rioproject.core.OperationalString
import org.rioproject.core.ServiceBeanConfig
import org.rioproject.core.ServiceElement
import org.rioproject.cybernode.Cybernode
import org.rioproject.cybernode.CybernodeAdmin
import org.rioproject.monitor.DeployAdmin
import org.rioproject.monitor.ProvisionMonitor
import org.rioproject.resources.client.JiniClient
import org.springframework.stereotype.Service
import com.elasticgrid.utils.logging.Log

/**
 * {@link ClusterLocator} based on EC2 Security Groups, as described on Elastic Grid Blog post:
 * http://blog.elastic-grid.com/2008/06/30/how-to-do-some-service-discovery-on-amazon-ec2/
 */
@Service
class JiniGroupsClusterLocator extends LANClusterLocator {
  def JiniClient jiniClient
  def ServiceDiscoveryManager sdm
  def LookupCache monitorsCache
  def LookupCache agentsCache

  public JiniGroupsClusterLocator() {
    def groups = JiniClient.parseGroups(System.getProperty('org.rioproject.groups', "all"))
    def locators = JiniClient.parseLocators(System.getProperty('org.rioproject.locator'))
    def config = ConfigurationProvider.getInstance(['-'] as String[])
    jiniClient = new JiniClient(new LookupDiscoveryManager(groups, locators, null, config))
    def monitors = new ServiceTemplate(null, [ProvisionMonitor.class] as Class[], null)
    def agents = new ServiceTemplate(null, [Cybernode.class] as Class[], null)
    sdm = new ServiceDiscoveryManager(jiniClient.getDiscoveryManager(), new LeaseRenewalManager(), config)
    monitorsCache = sdm.createLookupCache(monitors, null, null)
    agentsCache = sdm.createLookupCache(agents, null, null)
  }

  public Set<String> findClusters() {
    def clusters = new HashSet()
    def ServiceItem[] items = agentsCache.lookup(null, Integer.MAX_VALUE)
    if (items.length == 0)
      items = sdm.lookup(new ServiceTemplate(null, [Cybernode.class] as Class[], null), Integer.MAX_VALUE, null)
    items.each { ServiceItem item ->
      def CybernodeAdmin cybernode = item.service.admin
      def ServiceElement serviceElement = cybernode.serviceElement
      def ServiceBeanConfig config = serviceElement.serviceBeanConfig
      config.groups.each { clusters << it }
    }
    Log.info "Found clusters $clusters"
    return clusters as Set<String>
  }

  public Set<LANNode> findNodes(String clusterName) throws ClusterNotFoundException, ClusterException {
    Log.info "Searching for Elastic Grid nodes in cluster '$clusterName'..."

    def filter = new AgentGroupFilter(clusterName)
    def ServiceItem[] agentsItems = agentsCache.lookup(filter, Integer.MAX_VALUE)
    if (agentsItems == null)
      agentsItems = sdm.lookup(new ServiceTemplate(null, [Cybernode.class] as Class[], null), Integer.MAX_VALUE, filter)
    filter = new MonitorGroupFilter(clusterName)
    def ServiceItem[] monitorsItems = monitorsCache.lookup(filter, Integer.MAX_VALUE)
    if (monitorsItems == null)
      monitorsItems = sdm.lookup(new ServiceTemplate(null, [ProvisionMonitor.class] as Class[], null), Integer.MAX_VALUE, filter)

    def Set<LANNode> nodes = new HashSet<LANNode>()
    monitorsItems.each { ServiceItem item ->
      def attributes = item.attributeSets
      def Host hostEntry = (Host) attributes.find { it instanceof Host }
      nodes << new LANNodeImpl()
              .instanceID(item.serviceID.toString())
              .profile(NodeProfile.MONITOR_AND_AGENT)
              .address(InetAddress.getByName(hostEntry.hostName))
    }
    agentsItems.each { ServiceItem item ->
      def attributes = item.attributeSets
      def Host hostEntry = (Host) attributes.find { it instanceof Host }
      def matches = agentsItems.findAll { it.attributeSets.find { it instanceof Host } == hostEntry }
      if (matches.size() > 1)
        nodes << new LANNodeImpl()
                .instanceID(item.serviceID.toString())
                .profile(NodeProfile.AGENT)
                .address(InetAddress.getByName(hostEntry.hostName))
    }
    Log.info "Found nodes $nodes"
    return nodes
  }

  public LANNode findMonitor(String clusterName) throws ClusterMonitorNotFoundException {
    Log.info "Searching for monitor node in cluster '$clusterName'..."
    def ServiceItem[] monitorsItems = monitorsCache.lookup(new MonitorGroupFilter(clusterName), Integer.MAX_VALUE);
    def ServiceItem item = (ServiceItem) monitorsItems[0]
    def attributes = item.attributeSets
    def Host hostEntry = (Host) attributes.find { it instanceof Host }
    return (LANNode) new LANNodeImpl()
            .instanceID(item.serviceID.toString())
            .profile(NodeProfile.MONITOR_AND_AGENT)
            .address(InetAddress.getByName(hostEntry.hostName))
  }

  public Set<Application> findApplications(String clusterName) throws ClusterException {
    Log.info "Searching for monitor node in cluster '$clusterName'..."
    def ServiceItem[] monitorsItems = monitorsCache.lookup(new MonitorGroupFilter(clusterName), Integer.MAX_VALUE);
    if (monitorsItems.length == 0) {
      return [] as Set<Application>
    }
    def ServiceItem item = (ServiceItem) monitorsItems[0]
    def ProvisionMonitor monitor = item.service as ProvisionMonitor
    def DeployAdmin dAdmin = monitor.admin as DeployAdmin

    Log.info "Found ${dAdmin.operationalStringManagers.length} opstrings"

    return dAdmin.operationalStringManagers.collect {
      def OperationalString opstring = it.operationalString
      Log.info "Found application ${it.operationalString.name}"
      def Application application = new ApplicationImpl().name(opstring.name)
      opstring.services.each { ServiceElement elem ->
        Log.info "Found service ${elem.serviceBeanConfig.name}"
        Log.finest "Found service '${elem}'"
        application.service(elem.serviceBeanConfig.name)
      }
      return application
    } as Set<Application>
  }

  private ProvisionMonitor findLocalMonitor() {
    Registry registry = LocateRegistry.getRegistry()
    return registry.lookup("Monitor") as ProvisionMonitor
  }

}
