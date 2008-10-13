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
import com.elasticgrid.model.ClusterProvisioning;
import com.elasticgrid.model.ec2.EC2Cluster;
import com.elasticgrid.model.internal.Clusters;
import org.jibx.runtime.JiBXException;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.ext.jibx.JibxRepresentation;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.WadlResource;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Component
@Scope("prototype")
public class ClustersResource extends WadlResource {
    @Autowired
    private ClusterManager<Cluster> clusterManager;

    @Override
    public void init(Context context, Request request, Response response) {
        super.init(context, request, response);
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
        List<Cluster> clusters;
        try {
            clusters = clusterManager.findClusters();
            return new JibxRepresentation<Clusters>(MediaType.APPLICATION_XML, new Clusters(clusters), "ElasticGridREST");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE, e);
        }
    }

    /**
     * Handle PUT requests: start a new cluster.
     */
    @Override
    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
    public void acceptRepresentation(Representation entity) throws ResourceException {
        String clusterName = null;
        int numberOfMonitors = 1;
        int numberOfAgents = 0;
        if (MediaType.APPLICATION_XML.equals(entity.getMediaType())) {
            try {
                JibxRepresentation<ClusterProvisioning> representation =
                        new JibxRepresentation<ClusterProvisioning>(entity, ClusterProvisioning.class, "ElasticGridREST");
                ClusterProvisioning clusterProvisioning = representation.getObject();
                clusterName = clusterProvisioning.getClusterName();
                numberOfMonitors = clusterProvisioning.getNumberOfMonitors();
                numberOfAgents = clusterProvisioning.getNumberOfAgents();
            } catch (JiBXException e) {
                e.printStackTrace();
                throw new ResourceException(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY, e);
            } catch (IOException e) {
                e.printStackTrace();
                throw new ResourceException(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY, e);
            }
        } else if (MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType())) {
            Form form = new Form(entity);
            clusterName = form.getFirstValue("clusterName");
            numberOfMonitors = Integer.parseInt(form.getFirstValue("numberOfMonitors"));
            numberOfAgents = Integer.parseInt(form.getFirstValue("numberOfAgents"));
        }
        try {
            clusterManager.startCluster(clusterName, numberOfMonitors, numberOfAgents);
        } catch (TimeoutException e) {
            e.printStackTrace();
            throw new ResourceException(Status.SERVER_ERROR_GATEWAY_TIMEOUT, e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
        }
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
    protected void describePost(MethodInfo info) {
        super.describePost(info);
        info.setDocumentation("Start a new Elastic Grid Cluster.");
        info.getRequest().setDocumentation("The cluster to start.");
        RepresentationInfo xmlRepresentation = new RepresentationInfo();
        xmlRepresentation.setDocumentation("This representation exposes a provisioning request for starting a new Elastic Grid Cluster.");
        xmlRepresentation.getDocumentations().get(0).setTitle("cluster-provisioning");
        xmlRepresentation.setMediaType(MediaType.APPLICATION_XML);
        xmlRepresentation.getDocumentations().addAll(Arrays.asList(
                new DocumentationInfo("Example of input:<pre><![CDATA[" +
                        "<cluster-provisioning name=\"my-cluster\" xmlns=\"http://aws.amazon.com/ec2\">\n" +
                        "\t<!-- Start 2 monitors -->\n" +
                        "\t<monitors>2</monitors>\n" +
                        "\t<!-- Start 3 agents -->\n" +
                        "\t<agents>3</agents>\n" +
                        "</cluster-provisioning>" +
                        "]]></pre>")
        ));
        RepresentationInfo formRepresentation = new RepresentationInfo();
        formRepresentation.setDocumentation("This representation exposes a provisioning request for starting a new Elastic Grid Cluster.");
        formRepresentation.getDocumentations().get(0).setTitle("cluster-provisioning");
        formRepresentation.setMediaType(MediaType.APPLICATION_WWW_FORM);
        formRepresentation.setParameters(Arrays.asList(
                new ParameterInfo("clusterName", true, "xs:string", ParameterStyle.PLAIN,
                        "The name of the Elastic Grid Cluster to start."),
                new ParameterInfo("numberOfMonitors", true, "xs:integer", ParameterStyle.PLAIN,
                        "The number of monitors to start in the cluster."),
                new ParameterInfo("numberOfAgents", true, "xs:integer", ParameterStyle.PLAIN,
                        "The number of agents to start in the cluster.")
        ));
        info.getRequest().setRepresentations(Arrays.asList(xmlRepresentation, formRepresentation));
    }

    @Override
    public boolean allowDelete() {
        return false;
    }

    @Override
    public boolean allowPut() {
        return false;
    }
}
