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
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public class ClusterResource extends WadlResource {
    private String clusterName;
    private ClusterManager clusterManager;

    public ClusterResource(Context context, Request request, Response response) {
        super(context, request, response);
        clusterName = (String) getRequest().getAttributes().get("clusterName");
        getVariants().add(new Variant(MediaType.APPLICATION_XML));
    }

    @Override
    public Representation represent(Variant variant) throws ResourceException {
        Cluster cluster = new EC2ClusterImpl().name(clusterName);
        return new JibxRepresentation<Cluster>(MediaType.APPLICATION_XML, cluster, "ElasticGridREST");
//        try {
//            Cluster cluster = clusterManager.cluster(clusterName);
//            return new JibxRepresentation<Cluster>(MediaType.APPLICATION_XML, cluster, "ElasticGridREST");
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new ResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE, e);
//        }
    }

}