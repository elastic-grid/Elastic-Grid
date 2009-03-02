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

import com.elasticgrid.model.Cluster;
import com.elasticgrid.model.ClusterException;
import com.elasticgrid.model.Node;
import com.noelios.restlet.application.EncodeRepresentation;
import org.restlet.Client;
import org.restlet.data.Encoding;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.FileRepresentation;
import org.rioproject.tools.cli.OptionHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.util.StringTokenizer;
import java.util.Set;

/**
 * Install an OAR through the REST API.
 * @author Jerome Bernard
 */
public class InstallHandler extends AbstractHandler implements OptionHandler {

    /**
     * Process the option.
     *
     * @param input Parameters for the option, may be null
     * @param br An optional BufferedReader, used if the option requires input.
     * if this is null, the option handler may create a BufferedReader to handle the input
     * @param out The PrintStream to use if the option prints results or
     * choices for the user. Must not be null
     *
     * @return The result of the action.
     */
    public String process(String input, BufferedReader br, PrintStream out) {
        StringTokenizer tok = new StringTokenizer(input);
        if (tok.countTokens() == 3) {
            // first token is "install"
            tok.nextToken();
            // second token is the cluster name
            String clusterName = tok.nextToken();
            // second token is the path to the OAR
            String oarName = tok.nextToken();
            try {
                install(clusterName, oarName);
                return "Installed OAR '" + oarName + "' in cluster '" + clusterName + "'";
            } catch (Exception e) {
                e.printStackTrace(out);
                return "unexpected exception";
            }
        } else {
            return getUsage();
        }
    }

    private void install(String clusterName, String oarName) throws ClusterException, RemoteException {
        Cluster<Node> cluster = CLI.getClusterManager().cluster(clusterName);
        if (cluster == null) {
            System.err.println("Could not locate cluster '" + clusterName + "'");
            return;
        }
        Set<Node> monitors = cluster.getMonitorNodes();
        if (monitors.size() == 0) {
            System.err.println("Could not find any monitor");
            return;
        }
        Node monitor = monitors.iterator().next();
        System.out.println("Found monitor at " + monitor.getAddress());

        FileRepresentation rep = new FileRepresentation(oarName, new MediaType("application/oar"), 0);
        EncodeRepresentation encodedRep = new EncodeRepresentation(Encoding.GZIP, rep);
        Client client = new Client(Protocol.HTTP);
        Request request = new Request(Method.PUT, "http://" + monitor.getAddress() +":8182/eg/" + clusterName + "/applications");
        request.setEntity(encodedRep);
        Form form = new Form();
        form.add("x-filename", new File(oarName).getName());
        request.getAttributes().put("org.restlet.http.headers", form);
        Response response = client.handle(request);
        System.out.println("Received status " + response.getStatus());
    }

    /**
     * Get the usage of the command
     *
     * @return Command usage
     */
    public String getUsage() {
        return("usage: install clusterName path_to_oar\n");
    }

}