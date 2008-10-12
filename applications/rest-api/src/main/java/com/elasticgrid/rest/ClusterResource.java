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
import org.restlet.data.Reference;
import org.restlet.ext.jibx.JibxRepresentation;
import org.restlet.ext.wadl.WadlResource;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;
import java.util.Arrays;

@Component
@Scope("prototype")
public class ClusterResource extends WadlResource {
    private String clusterName;
    @Autowired
    private ClusterManager clusterManager;

    @Override
    public void init(Context context, Request request, Response response) {
        super.init(context, request, response);
        // Allow modifications of this resource via POST requests
        setModifiable(true);
        setAutoDescribed(true);
        // Declare the kind of representations supported by this resource
        getVariants().add(new Variant(MediaType.APPLICATION_XML));
        // Extract URI variables
        clusterName = (String) request.getAttributes().get("clusterName");
    }

    /**
     * Handle GET requests: describe cluster.
     */
    @Override
    public Representation represent(Variant variant) throws ResourceException {
        try {
            Cluster cluster = clusterManager.cluster(clusterName);
            return new JibxRepresentation<Cluster>(MediaType.APPLICATION_XML, cluster, "ElasticGridREST");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE, e);
        }
    }

    /**
     * Handle POST requests: update cluster.
     */
    @Override
    public void acceptRepresentation(Representation entity) throws ResourceException {
        // TODO: update the cluster!
    }

    /**
     * Handle DELETE requests: stop the cluster.
     */
    @Override
    public void removeRepresentations() throws ResourceException {
        // TODO: stop the cluster!
    }

    @Override
    protected void describeGet(MethodInfo info) {
        super.describeGet(info);
        info.setDocumentation("Describe cluster {clusterName}.");
        info.getResponse().setDocumentation("The cluster.");
        RepresentationInfo representation = new RepresentationInfo();
        representation.setDocumentation("This resource exposes cluster {clusterName}.");
        representation.getDocumentations().get(0).setTitle("cluster");
        representation.setMediaType(MediaType.APPLICATION_XML);
        representation.getDocumentations().addAll(Arrays.asList(
                new DocumentationInfo("Example of output:<pre><![CDATA[" +
                        "<cluster name=\"cluster1\">\n" +
                        "  <node profile=\"monitor\">ec2-75...</node>\n" +
                        "  <node profile=\"monitor\">ec2-77...</node>\n" +
                        "  <node profile=\"agent\">ec2-37...</node>\n" +
                        "  <node profile=\"agent\">ec2-33...</node>\n" +
                        "  <node profile=\"agent\">ec2-32...</node>\n" +
                        "</cluster>" +
                        "]]></pre>")
        ));
        info.getResponse().setRepresentations(Arrays.asList(representation));
    }

    @Override
    protected void describePost(MethodInfo info) {
        super.describePut(info);
        info.setDocumentation("Update {clusterName} cluster.");
    }

    @Override
    protected void describeDelete(MethodInfo info) {
        super.describeDelete(info);
        info.setDocumentation("Stop {clusterName} cluster.");
    }

    @Override
    public boolean allowPut() {
        return false;
    }

}