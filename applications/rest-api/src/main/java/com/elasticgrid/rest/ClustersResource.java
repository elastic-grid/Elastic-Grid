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
import com.elasticgrid.model.ec2.impl.EC2ClusterImpl;
import com.elasticgrid.model.internal.Clusters;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.jibx.JibxRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.WadlResource;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.apache.commons.lang.StringUtils;
import java.util.Arrays;
import java.util.HashMap;

public class ClustersResource extends WadlResource {
    private ClusterManager clusterManager;

    public ClustersResource(Context context, Request request, Response response) {
        super(context, request, response);
        // Allow modifications of this resource via POST requests
        setModifiable(true);
        // Declare the kind of representations supported by this resource
        getVariants().add(new Variant(MediaType.APPLICATION_XML));
    }

    /**
     * Handle GET requests: describe all clusters.
     */
    @Override
    public Representation represent(Variant variant) throws ResourceException {
        Clusters clusters = new Clusters();
        clusters.addCluster(new EC2ClusterImpl().name("cluster1"));
        clusters.addCluster(new EC2ClusterImpl().name("cluster2"));
        clusters.addCluster(new EC2ClusterImpl().name("cluster3"));
        return new JibxRepresentation<Clusters>(MediaType.APPLICATION_XML, clusters, "ElasticGridREST");
//        List<Cluster> clusters = null;
//        try {
//            clusters = clusterManager.findClusters();
//            return new JibxRepresentation<Clusters>(MediaType.APPLICATION_XML, new Clusters(clusters), "ElasticGridREST");
//        } catch (Exception e) {
//            throw new ResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE, e);
//        }
    }

    /**
     * Handle PUT requests: start a new cluster.
     */
    @Override
    public void storeRepresentation(Representation entity) throws ResourceException {
        super.acceptRepresentation(entity);
    }

    @Override
    protected void describeGet(MethodInfo info) {
        super.describeGet(info);
        info.setDocumentation("Describe all Elastic Grid clusters.");
        info.getResponse().setDocumentation("The clusters.");
        RepresentationInfo representation = new RepresentationInfo();
        representation.setDocumentation("This representation exposes all running Elastic Grid Clusters.");
        representation.getDocumentations().get(0).setTitle("clusters");
        representation.setMediaType(MediaType.APPLICATION_XML);
        representation.getDocumentations().addAll(Arrays.asList(
                new DocumentationInfo("Example of output:<pre><![CDATA[" +
                        "<clusters>\n" +
                        "  <cluster name=\"cluster1\">\n" +
                        "    <node profile=\"monitor\">ec2-75...</node>\n" +
                        "    <node profile=\"monitor\">ec2-77...</node>\n" +
                        "    <node profile=\"agent\">ec2-37...</node>\n" +
                        "  </cluster>\n" +
                        "  <cluster name=\"cluster2\">\n" +
                        "    <node profile=\"monitor\">ec2-57...</node>\n" +
                        "    <node profile=\"monitor\">ec2-63...</node>\n" +
                        "    <node profile=\"agent\">ec2-31...</node>\n" +
                        "  </cluster>\n" +
                        "</clusters>" +
                        "]]></pre>")
        ));
        info.getResponse().setRepresentations(Arrays.asList(representation));
    }

    @Override
    protected void describePut(MethodInfo info) {
        super.describePost(info);
        info.setDocumentation("Start a new cluster.");
        info.getRequest().setDocumentation("The cluster to start.");
        RepresentationInfo representation = new RepresentationInfo();
        representation.setDocumentation("This representation exposes a request for starting a new Elastic Grid Cluster.");
        representation.getDocumentations().get(0).setTitle("cluster-request");
        representation.setMediaType(MediaType.APPLICATION_XML);
        representation.getDocumentations().addAll(Arrays.asList(
                new DocumentationInfo("Example of input:<pre><![CDATA[" +
                        "<cluster name=\"my-cluster\">\n" +
                        "  <provisioning>\n" +
                        "    <!-- Start 2 monitors -->\n" +
                        "    <monitors>2</monitors>\n" +
                        "    <!-- Start 3 agents -->\n" +
                        "    <agents>3</agents>\n" +
                        "  </provisioning>\n" +
                        "</cluster>" +
                        "]]></pre>")
        ));
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
