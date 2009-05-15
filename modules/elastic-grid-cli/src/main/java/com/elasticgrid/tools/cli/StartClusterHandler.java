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

package com.elasticgrid.tools.cli;

import com.elasticgrid.cluster.NodeProfileInfo;
import com.elasticgrid.model.ClusterAlreadyRunningException;
import com.elasticgrid.model.NodeProfile;
import com.elasticgrid.model.NodeType;
import com.elasticgrid.model.ec2.EC2NodeType;
import org.rioproject.tools.cli.OptionHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class StartClusterHandler extends AbstractHandler
    implements OptionHandler {

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
    public String process(String input, BufferedReader br, PrintStream out) {
        StringTokenizer tok = new StringTokenizer(input);
        // first token is "start-cluster"
        tok.nextToken();
        // second token is cluster name
        String clusterName = null;
        if (tok.hasMoreTokens())
            clusterName = tok.nextToken();
        List<NodeProfileInfo> clusterInfo = new ArrayList<NodeProfileInfo>();
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
            clusterInfo.addAll(interactiveClusterSetup(br, out));
        } else if (!tok.hasMoreTokens()) {
            clusterInfo.addAll(interactiveClusterSetup(br, out));
        } else {
            while (tok.hasMoreTokens()) {
                String value = tok.nextToken();
                if (value.startsWith("mon:")) {
                    if (!processOption(value.substring(4),
                                       clusterInfo,
                                       NodeProfile.MONITOR,
                                       out))
                        return getUsage();

                } else if (value.startsWith("agent:")) {
                    if (!processOption(value.substring(6),
                                       clusterInfo,
                                       NodeProfile.AGENT,
                                       out))
                        return getUsage();
                } else if (value.startsWith("monAgent:")) {
                    if (!processOption(value.substring(9),
                                       clusterInfo,
                                       NodeProfile.MONITOR_AND_AGENT,
                                       out))
                        return getUsage();
                } else {
                    out.println("Unknown option " + value);
                    return getUsage();
                }
            }
        }

        if (clusterName.length() == 0)
            return ("Cluster must have a name, start-cluster cancelled");
        int numMonitors = countMonitors(clusterInfo);
        int numAgents = countAgents(clusterInfo);
        int numMonitorAgents = countMonitorAgents(clusterInfo);
        if (numMonitors > 0 || numAgents > 0 || numMonitorAgents > 0) {
            try {
                out.println("\nStarting cluster [" + clusterName + "] ...");
                getClusterManager().startCluster(clusterName,
                                                 numMonitors,
                                                 numMonitorAgents,
                                                 numAgents);
                return "Cluster [" + clusterName + "] started with " +
                       numMonitors + " Monitor(s), " +
                       numAgents + " Agent(s), " +
                       numMonitorAgents + " Monitor Agent(s)";
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
                "[monAgent:count[:s|m|l|xl]] ]\n");
    }

    private boolean processOption(String option,
                                  List<NodeProfileInfo> clusterInfo,
                                  NodeProfile nodeProfile,
                                  PrintStream out) {
        int num;
        try {
            num = getNumber(option);
        } catch (NumberFormatException e) {
            out.println("Not a valid number: " + option);
            return false;
        }
        String type = getType(option);
        if (type == null) {
            out.println("Bad type: " + option);
            return false;
        }
        addNodeProfileInfo(clusterInfo,
                           nodeProfile,
                           translateToNodeType(type),
                           num);
        return true;
    }

    private int getNumber(String s) {
        int number;
        int ndx = s.indexOf(":");
        if (ndx > 0)
            s = s.substring(0, ndx);
        number = Integer.parseInt(s);
        return number;
    }

    private String getType(String s) {
        String type = null;
        int ndx = s.indexOf(":");
        if (ndx > 0) {
            s = s.substring(ndx + 1);
            String[] types = {"s", "m", "l", "xl"};
            for (String t : types) {
                if (t.equals(s)) {
                    type = s;
                    break;
                }
            }
        } else {
            type = "s";
        }
        return type;
    }

    private void addNodeProfileInfo(List<NodeProfileInfo> clusterInfo,
                                    NodeProfile nodeProfile,
                                    NodeType nodeType,
                                    int number) {
        clusterInfo.add(new NodeProfileInfo(nodeProfile, nodeType, number));
    }

    private String getClusterName(BufferedReader br, PrintStream out)
        throws IOException {
        return getResponse("Enter name of cluster: ", br, out);
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

    private String getResponse(String ask, BufferedReader br, PrintStream out)
        throws IOException {
        out.print(ask);
        return br.readLine();
    }

    private NodeType translateToNodeType(String type) {
        NodeType nType;
        if (type.equals("s")) {
            nType = EC2NodeType.SMALL;
        } else if (type.equals("m")) {
            nType = EC2NodeType.MEDIUM_HIGH_CPU;
        } else if (type.equals("l")) {
            nType = EC2NodeType.LARGE;
        } else {
            nType = EC2NodeType.EXTRA_LARGE;
        }
        return nType;
    }

    private String getValidResponse(String ask,
                                    String[] validResponse,
                                    String def,
                                    BufferedReader br,
                                    PrintStream out)
        throws IOException {
        String response = null;
        while (true) {
            out.print(ask);
            String s = br.readLine();
            if (s == null)
                break;
            for (String v : validResponse) {
                if (v.equals(s)) {
                    response = s;
                    break;
                }
            }
            if (response != null)
                break;
            else
                out.println("Invalid response [" + s + "]");
        }
        return response == null ? def : response;
    }

    private int getIntegerResponse(String ask,
                                   BufferedReader br,
                                   PrintStream out)
        throws IOException {
        int response = -1;
        while (true) {
            out.print(ask);
            String s = br.readLine();
            if (s == null)
                break;
            if (s.length() == 0)
                break;
            try {
                response = Integer.parseInt(s);
                if (response < 0)
                    out.println("Invalid number [" + s + "]");
                else
                    break;
            } catch (NumberFormatException e) {
                out.println("Invalid number [" + s + "]");
            }
        }
        return response;
    }

    private int countAgents(List<NodeProfileInfo> list) {
        int count = 0;
        for (NodeProfileInfo ci : list) {
            if (ci.getNodeProfile().equals(NodeProfile.AGENT))
                count += ci.getNumber();
        }
        return count;
    }

    private int countMonitors(List<NodeProfileInfo> list) {
        int count = 0;
        for (NodeProfileInfo ci : list) {
            if (ci.getNodeProfile().equals(NodeProfile.MONITOR))
                count += ci.getNumber();
        }
        return count;
    }

    private int countMonitorAgents(List<NodeProfileInfo> list) {
        int count = 0;
        for (NodeProfileInfo ci : list) {
            if (ci.getNodeProfile().equals(NodeProfile.MONITOR_AND_AGENT))
                count += ci.getNumber();
        }
        return count;
    }
}
