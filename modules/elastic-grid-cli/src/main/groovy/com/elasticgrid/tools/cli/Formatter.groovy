/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * Licensed under the GNU Lesser General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.elasticgrid.tools.cli

import com.elasticgrid.model.Grid
import com.elasticgrid.model.ec2.EC2Node
import com.elasticgrid.model.NodeProfile

class Formatter {

    def static printGrids(List<Grid> grids, BufferedReader br, PrintStream out) {
        out.println "total: ${grids.size()}"
        grids.eachWithIndex { Grid grid, index ->
            out.println "[${index + 1}]\t${grid.name}"
            grid.nodes.eachWithIndex { com.elasticgrid.model.Node node, nodeIndex ->
                def profile
                if (NodeProfile.MONITOR == node.profile)
                    profile = "Monitor"
                else if (NodeProfile.AGENT == node.profile)
                    profile = "Agent"
                if (node instanceof EC2Node) {
                    out.println "\t[${nodeIndex + 1}] ${profile}\t${node.address}\t${node.instanceID}"
                } else {
                    out.println "\t[${nodeIndex + 1}] ${profile}\t${node.address}"
                }
            }
        }
    }

}