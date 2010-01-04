/**
 * Elastic Grid
 * Copyright (C) 2008-2010 Elastic Grid, LLC.
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
        Cluster cluster = clusterFactory.createCluster().name("cluster");
        Application app1 = cluster.application("app1");
        app1.service("serv1");
        app1.service("serv2");
        Application app2 = cluster.application("app2");
        app2.service("serv3");
        app2.service("serv4");
        clusters.addCluster(cluster);
        String xml = XmlUtils.convertObjectToXml("ElasticGridREST", clusters, "UTF-8");
        System.out.printf("XML is: %s\n", xml);
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
