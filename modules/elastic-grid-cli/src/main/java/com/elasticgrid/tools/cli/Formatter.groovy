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
package com.elasticgrid.tools.cli

import com.elasticgrid.model.Cluster
import com.elasticgrid.model.Node
import com.elasticgrid.model.NodeProfile
import com.elasticgrid.model.ec2.EC2Node
import com.elasticgrid.tools.cli.CLI
import net.jini.core.discovery.LookupLocator
import net.jini.discovery.DiscoveryLocatorManagement
import net.jini.discovery.DiscoveryManagement

class Formatter {

    def static printClusters(Set<Cluster> clusters, BufferedReader br, PrintStream out) {
        out.println "total: ${clusters.size()}"
        clusters.eachWithIndex { Cluster cluster, index ->
            out.println "[${index + 1}]\t${cluster.name}"
            //def locators = []
            def nodeIndex = 0;
            cluster.nodes.each { Node node ->
                def profile
                if (NodeProfile.MONITOR == node.profile) {
                    profile = "Monitor"
                    //locators << new LookupLocator("jini://${node.address.hostName}")
                } else if (NodeProfile.MONITOR_AND_AGENT == node.profile) {
                    profile = "Monitor+Agent"
                    //locators << new LookupLocator("jini://${node.address.hostName}")
                } else if (NodeProfile.AGENT == node.profile) {
                    profile = "Agent"
                }
                if (node instanceof EC2Node) {
                    out.println "\t[${++nodeIndex}] ${profile}\t${node.address}\t${node.instanceID}"
                } else {
                    out.println "\t[${++nodeIndex}] ${profile}\t${node.address}"
                }
            }
            /*
            DiscoveryManagement dMgr = CLI.instance.getServiceFinder().getDiscoveryManagement();
            if (dMgr instanceof DiscoveryLocatorManagement) {
                if (!locators) {
                    ((DiscoveryLocatorManagement) dMgr).setLocators(new LookupLocator[0]);
                } else {
                    try {
                        ((DiscoveryLocatorManagement) dMgr).addLocators locators as LookupLocator[]
                    } catch (MalformedURLException e) {
                        out.println("Bad locator format");
                    }
                }
            }
            */
        }
    }

}
