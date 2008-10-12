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
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import java.util.Arrays;
import java.util.HashMap;

@Component
@Scope("prototype")
public class ApplicationsResource extends WadlResource {
    @Autowired
    private ClusterManager clusterManager;

    @Override
    public void init(Context context, Request request, Response response) {
        super.init(context, request, response);
        // Allow modifications of this resource via POST requests
        setModifiable(true);
        // Declare the kind of representations supported by this resource
        getVariants().add(new Variant(MediaType.APPLICATION_XML));
    }

    /**
     * Handle GET requests: describe all applications.
     */
    @Override
    public Representation represent(Variant variant) throws ResourceException {
        return null;
    }

    /**
     * Handle PUT requests: provision a new application.
     */
    @Override
    public void storeRepresentation(Representation entity) throws ResourceException {
        super.acceptRepresentation(entity);
    }

    @Override
    protected void describeGet(MethodInfo info) {
        super.describeGet(info);
        info.setDocumentation("Describe all applications running on the cluster.");
        info.getResponse().setDocumentation("The applications.");
        RepresentationInfo representation = new RepresentationInfo();
        representation.setDocumentation("Applications");
        representation.setMediaType(MediaType.APPLICATION_XML);
        info.getResponse().setRepresentations(Arrays.asList(representation));
    }

    @Override
    protected void describePut(MethodInfo info) {
        super.describePost(info);
        info.setDocumentation("Provision a new application.");
        info.getRequest().setDocumentation("The application to provision.");
        RepresentationInfo representation = new RepresentationInfo();
        representation.setDocumentation("Application");
        representation.setMediaType(MediaType.APPLICATION_XML);
        info.getRequest().setRepresentations(Arrays.asList(representation));
    }

    @Override
    public boolean allowDelete() {
        return false;
    }

    @Override
    public boolean allowPost() {
        return false;
    }
}
