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
import com.elasticgrid.model.ClusterException;
import com.elasticgrid.model.internal.Clusters;
import org.jibx.runtime.JiBXException;
import org.restlet.Client;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.ext.jibx.JibxRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@ContextConfiguration(locations = {"/com/elasticgrid/rest/applicationContext.xml" })
public class RestTest extends AbstractTestNGSpringContextTests {
    private Client client;
    private static final String ROOT = "http://localhost:8182/eg";

    @Test
    public void testListOfClusters() throws IOException, ResourceException, JiBXException {
        Representation representation = get(client, new Reference(ROOT));
        Clusters clusters = new JibxRepresentation<Clusters>(representation, Clusters.class, "ElasticGridREST").getObject();
        assert clusters != null;
        assert clusters.getClusters().size() == 2;
    }

    /*
    @Test
    public void testProvisioningOfClusters() throws IOException, ResourceException, JiBXException {
        // create a new cluster
        ClusterProvisioning provisioningRequest = new ClusterProvisioning();
        provisioningRequest.setClusterName("my-cluster");
        provisioningRequest.setNumberOfMonitors(2);
        provisioningRequest.setNumberOfAgents(3);
        Representation request = new JibxRepresentation<ClusterProvisioning>(MediaType.APPLICATION_XML,
                provisioningRequest, "ElasticGridREST");
        post(client, new Reference(ROOT), request);
        Representation representation = get(client, new Reference(ROOT));
        Clusters clusters = new JibxRepresentation<Clusters>(representation, Clusters.class, "ElasticGridREST").getObject();
        assert clusters != null;
        assert clusters.getClusters().size() == 3;

        // update the cluster
        provisioningRequest.setNumberOfMonitors(12);
        provisioningRequest.setNumberOfAgents(100);
        request = new JibxRepresentation<ClusterProvisioning>(MediaType.APPLICATION_XML,
                provisioningRequest, "ElasticGridREST");
        post(client, new Reference(ROOT + "/my-cluster"), request);
        representation = get(client, new Reference(ROOT + "/my-cluster"));
        Cluster cluster = new JibxRepresentation<Cluster>(representation, EC2ClusterImpl.class, "ElasticGridREST").getObject();
        assert cluster != null;
        assert "my-cluster".equals(cluster.getName());
        assert cluster.getNodes().size() == 112;

        // delete the cluster
        delete(client, new Reference(ROOT + "/my-cluster"));
        representation = get(client, new Reference(ROOT));
        clusters = new JibxRepresentation<Clusters>(representation, Clusters.class, "ElasticGridREST").getObject();
        assert clusters != null;
        assert clusters.getClusters().size() == 2;
    }

    @Test
    public void testDescriptionOfCluster() throws IOException, ResourceException, JiBXException {
        Representation representation = get(client, new Reference(ROOT + "/fake1"));
        Cluster cluster = new JibxRepresentation<Cluster>(representation, EC2ClusterImpl.class, "ElasticGridREST").getObject();
        assert cluster != null;
        assert "fake1".equals(cluster.getName());
        assert cluster.getNodes().size() == 1;
    }
    */

    @BeforeTest
    public void setupRestClient() {
        // Define our Restlet HTTP client.
        client = new Client(Protocol.HTTP);
    }

    @BeforeMethod
    public void setupDummyClusters() throws TimeoutException, ExecutionException, RemoteException, ClusterException, InterruptedException {
        ClusterManager clusterManager = (ClusterManager) applicationContext.getBean("clusterManager");
        clusterManager.startCluster("fake1");
        clusterManager.startCluster("fake2");
    }

    /**
     * Send a GET request.
     *
     * @param client client Restlet.
     * @param reference the resource's URI.
     * @return the representation at the requested URL
     * @throws IOException if there is a networking issue
     * @throws ResourceException if the resource is invalid
     */
    private Representation get(Client client, Reference reference) throws ResourceException, IOException {
        Response response = client.get(reference);
        if (response.getStatus().isSuccess()) {
            if (response.isEntityAvailable()) {
                return response.getEntity();
            }
        }
        response.getEntity().write(System.err);
        throw new ResourceException(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY);
    }

    /**
     * Send a POST request.
     *
     * @param client client Restlet.
     * @param reference the resource's URI.
     * @param representation the representation to send as input
     * @throws IOException if there is a networking issue
     * @throws ResourceException if the resource is invalid
     */
    private void post(Client client, Reference reference, Representation representation) throws ResourceException, IOException {
        Response response = client.post(reference, representation);
        if (!response.getStatus().isSuccess()) {
            throw new ResourceException(response.getStatus());
        }
    }

    /**
     * Send a DELETE request.
     *
     * @param client client Restlet.
     * @param reference the resource's URI.
     * @throws IOException if there is a networking issue
     * @throws ResourceException if the resource is invalid
     */
    private void delete(Client client, Reference reference) throws ResourceException, IOException {
        Response response = client.delete(reference);
        if (!response.getStatus().isSuccess()) {
            throw new ResourceException(response.getStatus());
        }
    }

}
