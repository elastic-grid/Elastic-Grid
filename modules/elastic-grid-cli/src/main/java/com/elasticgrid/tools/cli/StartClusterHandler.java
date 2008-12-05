/**
 * Elastic Grid
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
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

import com.elasticgrid.model.ClusterAlreadyRunningException;
import org.rioproject.tools.cli.OptionHandler;
import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.StringTokenizer;

public class StartClusterHandler extends AbstractHandler implements OptionHandler {

    /**
     * Process the option.
     *
     * @param input Parameters for the option, may be null
     * @param br An optional BufferdReader, used if the option requires input.
     * if this is null, the option handler may create a BufferedReader to handle the input
     * @param out The PrintStream to use if the option prints results or
     * choices for the user. Must not be null
     *
     * @return The result of the action.
     */
    public String process(String input, BufferedReader br, PrintStream out) {
        StringTokenizer tok = new StringTokenizer(input);
        if (tok.countTokens() > 1) {
            // first token is "start-cluster"
            tok.nextToken();
            // second token is cluster name
            String clusterName = tok.nextToken();
            int clusterSize = 1;
            if (tok.countTokens() == 1)
                clusterSize = Integer.parseInt(tok.nextToken());
            try {
                getClusterManager().startCluster(clusterName, clusterSize);
                return "Cluster started with " + clusterSize + " node(s)";
            } catch (ClusterAlreadyRunningException e) {
                return "cluster already running!";
            } catch (Exception e) {
                e.printStackTrace(out);
                return "unexpected cluster exception";
            }
        } else {
            return getUsage();
        }
    }

    /**
     * Get the usage of the command
     *
     * @return Command usage
     */
    public String getUsage() {
        return("usage: start-cluster clusterName [-s clusterSize]\n");
    }

}
