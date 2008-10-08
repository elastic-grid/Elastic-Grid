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
import org.restlet.Context;
import org.restlet.ext.jibx.JibxRepresentation;
import org.restlet.ext.wadl.WadlResource;
import com.elasticgrid.model.internal.Clusters;
import com.elasticgrid.model.ec2.impl.EC2ClusterImpl;
import com.elasticgrid.cluster.ClusterManager;

public class ClustersResource extends WadlResource {
    private ClusterManager clusterManager;

    public ClustersResource(Context context, Request request, Response response) {
        super(context, request, response);
        getVariants().add(new Variant(MediaType.APPLICATION_XML));
    }

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

}
