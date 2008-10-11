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
import com.elasticgrid.model.Cluster;
import com.elasticgrid.model.ec2.impl.EC2ClusterImpl;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.ext.jibx.JibxRepresentation;
import org.restlet.ext.wadl.WadlResource;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import java.util.Arrays;

public class ServiceResource extends WadlResource {
    private String clusterName;
    private String applicationName;
    private String serviceName;
    private ClusterManager clusterManager;

    public ServiceResource(Context context, Request request, Response response) {
        super(context, request, response);
        // Allow modifications of this resource via POST requests
        setModifiable(false);
        setAutoDescribed(true);
        // Declare the kind of representations supported by this resource
        getVariants().add(new Variant(MediaType.APPLICATION_XML));
        // Extract URI variables
        clusterName = (String) getRequest().getAttributes().get("clusterName");
        applicationName = (String) getRequest().getAttributes().get("applicationName");
        serviceName = (String) getRequest().getAttributes().get("serviceName");
    }

    /**
     * Handle GET requests: describe service.
     */
    @Override
    public Representation represent(Variant variant) throws ResourceException {
        return null;
    }

    @Override
    protected void describeGet(MethodInfo info) {
        super.describeGet(info);
        info.setDocumentation("Describes service {serviceName} from {applicationName} running on {clusterName}");
        info.getResponse().setDocumentation("The service.");
        RepresentationInfo representation = new RepresentationInfo();
        representation.setDocumentation("Service");
        representation.setMediaType(MediaType.APPLICATION_XML);
        info.getResponse().setRepresentations(Arrays.asList(representation));
    }

}