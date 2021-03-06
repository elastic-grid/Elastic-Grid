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
package com.elasticgrid.tools.cli;

import com.elasticgrid.model.Cluster;
import com.elasticgrid.model.ClusterAlreadyRunningException;
import com.elasticgrid.model.Node;
import com.elasticgrid.model.NodeProfile;
import com.elasticgrid.model.NodeProfileInfo;
import org.rioproject.tools.cli.OptionHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class StartClusterHandler extends AbstractClusterHandler implements OptionHandler {    

    /**
     * Process the option.
     *
     * @param input Parameters for the option, may be null
     * @param br An optional BufferdReader, used if the option requires input.
     * if this is null, the option handler may create a BufferedReader to handle
     * the input
     * @param out The PrintStream to use if the option prints results or choices
     * for the user. Must not be null
     * @return The result of the action.
     */
    @SuppressWarnings("unchecked")
    public String process(String input, BufferedReader br, PrintStream out) {
        File overridesDir = null;
        StringTokenizer tok = new StringTokenizer(input);
        // first token is "start-cluster"
        tok.nextToken();
        // second token is cluster name
        String clusterName = null;
        if (tok.hasMoreTokens())
            clusterName = tok.nextToken();
        List<NodeProfileInfo> nodeProfileInfo = new ArrayList<NodeProfileInfo>();
        if (clusterName == null) {
            try {
                clusterName = getClusterName(br, out);
            } catch (IOException e) {
                e.printStackTrace();
                return "unexpected parsing exception, check log";
            }
            if (clusterName == null) {
                out.println("Cluster name not provided, start-cluster cancelled");
                return "";
            }
            nodeProfileInfo.addAll(interactiveClusterSetup(br, out));
        } else if (!tok.hasMoreTokens()) {
            nodeProfileInfo.addAll(interactiveClusterSetup(br, out));
        } else {

            while (tok.hasMoreTokens()) {
                String value = tok.nextToken();
                if (value.startsWith("mon:")) {
                    if (!processOption(value.substring(4),
                                       nodeProfileInfo,
                                       NodeProfile.MONITOR,
                                       out))
                        return getUsage();

                } else if (value.startsWith("agent:")) {
                    if (!processOption(value.substring(6),
                                       nodeProfileInfo,
                                       NodeProfile.AGENT,
                                       out))
                        return getUsage();
                } else if (value.startsWith("monAgent:")) {
                    if (!processOption(value.substring(9),
                                       nodeProfileInfo,
                                       NodeProfile.MONITOR_AND_AGENT,
                                       out))
                        return getUsage();
                } else {
                    /* process overrides*/
                    if(value.startsWith("~")) {
                        value = System.getProperty("user.home")+value.substring(1);
                    }

                    overridesDir = new File(value);
                    if(!overridesDir.exists() || !overridesDir.isDirectory())
                        return ("The ["+value+"] directory does not exist, or " +
                                "is not a directory. Please provide a valid " +
                                "location for cluster configuration overrides");
                    /* Check for groovy override configuration files */

                    for(String s : overridesDir.list()) {
                        if(s.equals(AGENT_OVERRIDE_FILE_NAME)) {
                            setHasOverride(NodeProfile.AGENT, nodeProfileInfo);
                            setHasOverride(NodeProfile.MONITOR_AND_AGENT, nodeProfileInfo);
                        }
                        if(s.equals(MONITOR_OVERRIDE_FILE_NAME)) {
                            setHasOverride(NodeProfile.MONITOR, nodeProfileInfo);
                            setHasOverride(NodeProfile.MONITOR_AND_AGENT, nodeProfileInfo);
                        }
                    }
                    //out.println("Unknown option " + value);
                    //return getUsage();
                }
            }
        }

        if (clusterName.length() == 0)
            return ("Cluster must have a name, start-cluster cancelled");
        int numMonitors = countMonitors(nodeProfileInfo);
        int numAgents = countAgents(nodeProfileInfo);
        int numMonitorAgents = countMonitorAgents(nodeProfileInfo);
        if (numMonitors > 0 || numAgents > 0 || numMonitorAgents > 0) {
            try {
                if(overridesDir!=null) {
                    uploadOverrides(overridesDir, clusterName, out);
                }
                out.println("\nStarting cluster [" + clusterName + "] ...");
                getClusterManager().startCluster(clusterName, nodeProfileInfo);
                StringBuilder sb = new StringBuilder();
                sb.append("Cluster [").append(clusterName).append("] started with:");
                Cluster c = getClusterManager().cluster(clusterName);
                Set<Node> nodes = c.getNodes();
                for(Node node : nodes) {
                    sb.append("\n");
                    sb.append("\t");
                    NodeProfile profile = node.getProfile();
                    if (profile.equals(NodeProfile.MONITOR)) {
                        sb.append("Monitor");
                    } else if (profile.equals(NodeProfile.MONITOR_AND_AGENT)) {
                        sb.append("Monitor+Agent");
                    } else if (profile.equals(NodeProfile.AGENT)) {
                        sb.append("Agent");
                    } else {
                        sb.append("Unknown");
                    }
                    sb.append("\t on ").append(node.getAddress());
                }
                return sb.toString();
            } catch (ClusterAlreadyRunningException e) {
                return "cluster already running!";
            } catch (Exception e) {
                e.printStackTrace(out);
                return "unexpected cluster exception";
            }
        } else {
            return ("Cluster [" + clusterName + "] has no configured cluster " +
                    "services, start-cluster cancelled");
        }

    }

    /**
     * Get the usage of the command
     *
     * @return Command usage
     */
    public String getUsage() {
        return ("usage: start-cluster [clusterName " +
                "[mon:count[:s|m|l|xl]] " +
                "[agent:count[:s|m|l|xl]] " +
                "[monAgent:count[:s|m|l|xl]] [overrides-dir] ]\n");
    }

    private List<NodeProfileInfo> interactiveClusterSetup(BufferedReader br,
                                                          PrintStream out) {
        List<NodeProfileInfo> list = new ArrayList<NodeProfileInfo>();
        try {
            if (br == null)
                br = new BufferedReader(new InputStreamReader(System.in));

            int numMonitors =
                getIntegerResponse("Number of Monitors: ", br, out);

            String[] types = {"s", "m", "l", "xl", ""};
            if (numMonitors > 0) {
                String monType =
                    getValidResponse("Monitor Machine type [s,m,l,xl] (s): ",
                                     types,
                                     "s",
                                     br,
                                     out);
                list.add(new NodeProfileInfo(NodeProfile.MONITOR,
                                             translateToNodeType(monType),
                                             numMonitors));
            }

            int numAgents =
                getIntegerResponse("Number of Agents: ", br, out);
            if (numAgents > 0) {
                String agentType =
                    getValidResponse("Agent Machine type [s,m,l,xl] (s): ",
                                     types,
                                     "s",
                                     br,
                                     out);
                list.add(new NodeProfileInfo(NodeProfile.AGENT,
                                             translateToNodeType(agentType),
                                             numAgents));
            }

            int numMonAgents =
                getIntegerResponse("Number of Monitor Agents: ", br, out);
            if (numMonAgents > 0) {
                String monAgentType =
                    getValidResponse("Monitor Agent Machine type [s,m,l,xl] (s): ",
                                     types,
                                     "s",
                                     br,
                                     out);
                list.add(new NodeProfileInfo(NodeProfile.MONITOR_AND_AGENT,
                                             translateToNodeType(monAgentType),
                                             numMonAgents));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }
}
