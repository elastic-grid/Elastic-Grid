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

import org.restlet.resource.Resource;
import org.restlet.resource.Variant;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.data.MediaType;
import org.restlet.data.Response;
import org.restlet.data.Request;
import org.restlet.data.Method;
import org.restlet.Context;
import org.restlet.ext.jibx.JibxRepresentation;
import org.restlet.ext.wadl.WadlResource;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.ext.wadl.ResourceInfo;
import com.elasticgrid.model.internal.Clusters;
import com.elasticgrid.model.ec2.impl.EC2ClusterImpl;
import com.elasticgrid.cluster.ClusterManager;

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
     * Handle POST requests: start a new cluster.
     * @param entity
     * @throws ResourceException
     */
    @Override
    public void acceptRepresentation(Representation entity) throws ResourceException {
        super.acceptRepresentation(entity);
    }

    @Override
    protected void describeGet(MethodInfo info) {
        super.describeGet(info);
        info.setDocumentation("Describe all Elastic Grid clusters.");
    }

    @Override
    protected void describePost(MethodInfo info) {
        super.describePost(info);
        info.setDocumentation("Start a new cluster.");
    }
}
