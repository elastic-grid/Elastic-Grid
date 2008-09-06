/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This file is part of Elastic Grid.
 * 
 * Elastic Grid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * Elastic Grid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Elastic Grid.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elasticgrid.tools.cli

import com.elasticgrid.model.Grid
import com.elasticgrid.model.NodeProfile
import com.elasticgrid.model.ec2.EC2Node
import net.jini.core.discovery.LookupLocator
import net.jini.discovery.DiscoveryLocatorManagement
import net.jini.discovery.DiscoveryManagement

class Formatter {

    def static printGrids(List<Grid> grids, BufferedReader br, PrintStream out) {
        out.println "total: ${grids.size()}"
        grids.eachWithIndex { Grid grid, index ->
            out.println "[${index + 1}]\t${grid.name}"
            def locators = []
            grid.nodes.eachWithIndex { com.elasticgrid.model.Node node, nodeIndex ->
                def profile
                if (NodeProfile.MONITOR == node.profile) {
                    profile = "Monitor"
                    locators << "jini://${node.address.hostName}"
                } else if (NodeProfile.AGENT == node.profile) {
                    profile = "Agent"
                }
                if (node instanceof EC2Node) {
                    out.println "\t[${nodeIndex + 1}] ${profile}\t${node.address}\t${node.instanceID}"
                } else {
                    out.println "\t[${nodeIndex + 1}] ${profile}\t${node.address}"
                }
            }
            DiscoveryManagement dMgr = CLI.instance.getServiceFinder().getDiscoveryManagement();
            println "Locators are $locators"
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
        }
    }

}