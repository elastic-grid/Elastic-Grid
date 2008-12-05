/**
 * Elastic Grid
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
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

package com.elasticgrid.model;

import com.elasticgrid.model.ec2.EC2Cluster;
import com.elasticgrid.model.ec2.impl.EC2ClusterImpl;
import com.elasticgrid.model.internal.LocalStore;
import com.elasticgrid.model.internal.RemoteStore;
import com.elasticgrid.model.internal.Clusters;
import com.elasticgrid.model.internal.jibx.ObjectXmlMappingException;
import com.elasticgrid.model.internal.jibx.XmlUtils;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Tests for the JiBX mapping.
 * @author Jerome Bernard
 */
public class JiBXTest {
    private ClusterFactory clusterFactory;

    @Test
    public void testRoundtripOfClusters() throws ObjectXmlMappingException, UnknownHostException {
        Clusters clusters = new Clusters();
        clusters.addCluster(clusterFactory.createCluster().name("cluster"));
        String xml = XmlUtils.convertObjectToXml("ElasticGridREST", clusters, "UTF-8");
        System.out.printf("XML is: %s\n", xml);
    }

    @Test
    public void testRoundtripOfLocalStore() throws ObjectXmlMappingException, UnknownHostException {
        LocalStore repository = new LocalStore();
        repository.cluster("test").cluster("another cluster");
        repository.application("test_application").application("another_fake_application");
        String xml = XmlUtils.convertObjectToXml("ElasticGrid", repository, "UTF-8");
        System.out.printf("XML is: %s\n", xml);
    }

    @Test
    public void testRoundtripOfRemoteStore() throws ObjectXmlMappingException, UnknownHostException {
        RemoteStore repository = new RemoteStore();
        repository.cluster("test").cluster("another cluster");
        repository.application("test_application").application("another_fake_application");
        String xml = XmlUtils.convertObjectToXml("ElasticGrid", repository, "UTF-8");
        System.out.printf("XML is: %s\n", xml);
    }

    @Test
    public Cluster testRoundtripOfEC2Grid() throws ObjectXmlMappingException, UnknownHostException {
        EC2Cluster cluster = (EC2Cluster) clusterFactory.createCluster().name("test-cluster");

        // add nodes        
        Node node1 = cluster.node("localhost", NodeProfile.MONITOR, InetAddress.getByName("localhost"));
        assertNotNull(node1);
        Node node2 = cluster.node("blog.elastic-grid.com", NodeProfile.MONITOR,
                InetAddress.getByName("blog.elastic-grid.com"));
        assertNotNull(node2);

        // add applications
        Application application1 = cluster.application("testApp1");
        application1.createNewOAR().name("oar1").version("1.0").opstring("opstring");
        assertNotNull(application1);
        Application application2 = cluster.application("testApp2");
        application2.createNewOAR().name("oar2").version("1.0").opstring("opstring");
        assertNotNull(application2);

        String xml = XmlUtils.convertObjectToXml("ElasticGrid", cluster, "UTF-8");
        assertNotNull(xml);
        System.out.printf("XML is: %s\n", xml);

        Cluster roundtrip = XmlUtils.convertXmlToObject("ElasticGrid", EC2ClusterImpl.class, new StringReader(xml));
        assertNotNull(roundtrip);
        assertEquals(roundtrip.getName(), cluster.getName());
        assertEquals(roundtrip.getNodes().size(), 2);
        assertEquals(roundtrip.getApplications().size(), 2);

        return cluster;
    }

    @BeforeTest
    public void setupClusterFactory() {
        clusterFactory = new ClusterFactory() {
            public Cluster createCluster() {
                return new EC2ClusterImpl();
            }
        };
    }

}
