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

package com.elasticgrid.rest;

import com.elasticgrid.cluster.ClusterManager;
import com.elasticgrid.model.Cluster;
import com.elasticgrid.model.ClusterProvisioning;
import com.elasticgrid.model.NodeProfileInfo;
import com.elasticgrid.model.NodeProfile;
import com.elasticgrid.model.ec2.EC2NodeType;
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
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.WadlResource;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

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
        String clusterName = null;
        NodeProfileInfo monitorsInfo = null;
        NodeProfileInfo monitorsAndAgentsInfo = null;
        NodeProfileInfo agentsInfo = null;
        if (MediaType.APPLICATION_XML.equals(entity.getMediaType())) {
            try {
                JibxRepresentation<ClusterProvisioning> representation =
                        new JibxRepresentation<ClusterProvisioning>(entity, ClusterProvisioning.class, "ElasticGridREST");
                ClusterProvisioning clusterProvisioning = representation.getObject();
                clusterName = clusterProvisioning.getClusterName();
                monitorsInfo = clusterProvisioning.getMonitorsInfo();
                monitorsAndAgentsInfo = clusterProvisioning.getMonitorsAndAgentsInfo();
                agentsInfo = clusterProvisioning.getAgentsInfo();
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
            monitorsInfo = new NodeProfileInfo(NodeProfile.MONITOR,
                    EC2NodeType.valueOf(form.getFirstValue("monitorNodeType")),
                    Integer.parseInt(form.getFirstValue("numberOfMonitors")));
            monitorsAndAgentsInfo = new NodeProfileInfo(NodeProfile.MONITOR_AND_AGENT,
                    EC2NodeType.valueOf(form.getFirstValue("monitorAndAgentNodeType")),
                    Integer.parseInt(form.getFirstValue("numberOfMonitorsAndAgents")));
            agentsInfo = new NodeProfileInfo(NodeProfile.AGENT,
                    EC2NodeType.valueOf(form.getFirstValue("agentNodeType")),
                    Integer.parseInt(form.getFirstValue("numberOfAgents")));
        }
        try {
            clusterManager.resizeCluster(clusterName, Arrays.asList(monitorsInfo, monitorsAndAgentsInfo, agentsInfo));
        } catch (TimeoutException e) {
            e.printStackTrace();
            throw new ResourceException(Status.SERVER_ERROR_GATEWAY_TIMEOUT, e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
        }
    }

    /**
     * Handle DELETE requests: stop the cluster.
     */
    @Override
    public void removeRepresentations() throws ResourceException {
        try {
            clusterManager.stopCluster(clusterName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Could not stop cluster " + clusterName, e);
        }
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
                        "<cluster name=\"cluster1\" xmlns=\"urn:elastic-grid:eg\">\n" +
                        "  <node profile=\"monitor\">ec2-75...</node>\n" +
                        "  <node profile=\"monitor\">ec2-77...</node>\n" +
                        "  <node profile=\"agent\">ec2-37...</node>\n" +
                        "  <node profile=\"agent\">ec2-33...</node>\n" +
                        "  <node profile=\"agent\">ec2-32...</node>\n" +
                        "</cluster>" +
                        "]]></pre>")
        ));
        representation.setXmlElement("eg:cluster");
        info.getResponse().setRepresentations(Arrays.asList(representation));
    }

    @Override
    protected void describePost(MethodInfo info) {
        super.describePut(info);
        info.setDocumentation("Update Elastic Grid Cluster {clusterName}.");
        info.getRequest().setDocumentation("The cluster to update.");
        RepresentationInfo xmlRepresentation = new RepresentationInfo();
        xmlRepresentation.setDocumentation("This representation exposes a provisioning request for updating an Elastic Grid Cluster.");
        xmlRepresentation.getDocumentations().get(0).setTitle("cluster-provisioning");
        xmlRepresentation.setMediaType(MediaType.APPLICATION_XML);
        xmlRepresentation.getDocumentations().addAll(Arrays.asList(
                new DocumentationInfo("Example of input:<pre><![CDATA[" +
                        "<cluster-provisioning name=\"my-cluster\" xmlns=\"urn:elastic-grid:eg\">\n" +
                        "\t<!-- Start 2 monitors -->\n" +
                        "\t<monitors>2</monitors>\n" +
                        "\t<!-- Start 4 monitors and agents -->\n" +
                        "\t<monitors-and-agents>4</monitors-and-agents>\n" +
                        "\t<!-- Start 3 agents -->\n" +
                        "\t<agents>3</agents>\n" +
                        "</cluster-provisioning>" +
                        "]]></pre>")
        ));
        xmlRepresentation.setXmlElement("eg:cluster-provisioning");
        RepresentationInfo formRepresentation = new RepresentationInfo();
        formRepresentation.setDocumentation("This representation exposes a provisioning request for updating an Elastic Grid Cluster.");
        formRepresentation.getDocumentations().get(0).setTitle("cluster-provisioning");
        formRepresentation.setMediaType(MediaType.APPLICATION_WWW_FORM);
        formRepresentation.setParameters(Arrays.asList(
                new ParameterInfo("clusterName", true, "xs:string", ParameterStyle.PLAIN,
                        "The name of the Elastic Grid Cluster to start."),
                new ParameterInfo("numberOfMonitors", true, "xs:integer", ParameterStyle.PLAIN,
                        "The number of monitors to start in the cluster."),
                new ParameterInfo("numberOfMonitorsAndAgents", true, "xs:integer", ParameterStyle.PLAIN,
                        "The number of 'monitors and agents' to start in the cluster."),
                new ParameterInfo("numberOfAgents", true, "xs:integer", ParameterStyle.PLAIN,
                        "The number of agents to start in the cluster.")
        ));
        info.getRequest().setRepresentations(Arrays.asList(xmlRepresentation, formRepresentation));
    }

    @Override
    protected void describeDelete(MethodInfo info) {
        super.describeDelete(info);
        info.setDocumentations(Arrays.asList(
                new DocumentationInfo("Stop {clusterName} cluster."),
                new DocumentationInfo("All nodes in the cluster will be shut down.")
        ));
    }

    @Override
    public boolean allowPut() {
        return false;
    }

}