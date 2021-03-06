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
package com.elasticgrid.rest;

import com.elasticgrid.cluster.ClusterManager;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.WadlResource;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import java.util.Arrays;

public class ApplicationResource extends WadlResource {
    private String clusterName;
    private String applicationName;
    private ClusterManager clusterManager;

    @Override
    public void init(Context context, Request request, Response response) {
        super.init(context, request, response);
        clusterManager = RestJSB.getClusterManager();
        // Allow modifications of this resource via POST requests
        setModifiable(true);
        setAutoDescribed(true);
        // Declare the kind of representations supported by this resource
        getVariants().add(new Variant(MediaType.APPLICATION_XML));
        // Extract URI variables
        clusterName = (String) request.getAttributes().get("clusterName");
        applicationName = (String) request.getAttributes().get("applicationName");
    }

    /**
     * Handle GET requests: describe cluster.
     */
    @Override
    public Representation represent(Variant variant) throws ResourceException {
        return null;
    }

    /**
     * Handle POST requests: update cluster.
     */
    @Override
    public void acceptRepresentation(Representation entity) throws ResourceException {
    }

    /**
     * Handle DELETE requests: stop the cluster.
     */
    @Override
    public void removeRepresentations() throws ResourceException {
    }

    @Override
    protected void describeGet(MethodInfo info) {
        super.describeGet(info);
        info.setDocumentation("Describes application {applicationName} running on {clusterName}");
        info.getResponse().setDocumentation("The application.");
        RepresentationInfo representation = new RepresentationInfo();
        representation.setDocumentation("Application");
        representation.setMediaType(MediaType.APPLICATION_XML);
        representation.setXmlElement("eg:application");
        info.getResponse().setRepresentations(Arrays.asList(representation));
    }

    @Override
    protected void describePost(MethodInfo info) {
        super.describePut(info);
        info.setDocumentation("Update {applicationName} cluster on cluster {clusterName}.");
    }

    @Override
    protected void describeDelete(MethodInfo info) {
        super.describeDelete(info);
        info.setDocumentation("Stop {applicationName} application running on cluster {clusterName}.");
    }

    @Override
    public boolean allowPut() {
        return false;
    }
}