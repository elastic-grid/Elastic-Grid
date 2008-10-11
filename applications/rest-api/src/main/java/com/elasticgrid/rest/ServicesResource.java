/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This file is part of Elastic Grid.
 * 
 * Elastic Grid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * 
 * Elastic Grid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Elastic Grid.  If not, see <http://www.gnu.org/licenses/>.
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
import java.util.HashMap;

public class ServicesResource extends WadlResource {
    private ClusterManager clusterManager;

    public ServicesResource(Context context, Request request, Response response) {
        super(context, request, response);
        // Allow modifications of this resource via POST requests
        setModifiable(false);
        // Declare the kind of representations supported by this resource
        getVariants().add(new Variant(MediaType.APPLICATION_XML));
    }

    /**
     * Handle GET requests: describe all services.
     */
    @Override
    public Representation represent(Variant variant) throws ResourceException {
        return null;
    }

    @Override
    protected void describeGet(MethodInfo info) {
        super.describeGet(info);
        info.setDocumentation("Describe all services of application {applicationName} running on cluster {clusterName}.");
        info.getResponse().setDocumentation("The services.");
        RepresentationInfo representation = new RepresentationInfo();
        representation.setDocumentation("Services");
        representation.setMediaType(MediaType.APPLICATION_XML);
        info.getResponse().setRepresentations(Arrays.asList(representation));
    }

}