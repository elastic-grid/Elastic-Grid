/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * Licensed under the GNU Lesser General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.elasticgrid.model;

import com.elasticgrid.model.ec2.EC2Grid;
import com.elasticgrid.model.ec2.impl.EC2GridImpl;
import com.elasticgrid.model.internal.LocalStore;
import com.elasticgrid.model.internal.RemoteStore;
import com.elasticgrid.utils.jibx.ObjectXmlMappingException;
import com.elasticgrid.utils.jibx.XmlUtils;
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
    private GridFactory gridFactory;

    @Test
    public void testRoundtripOfLocalStore() throws ObjectXmlMappingException, UnknownHostException {
        LocalStore repository = new LocalStore();
        repository.grid("test").grid("another grid");
        repository.application("test_application").application("another_fake_application");
        String xml = XmlUtils.convertObjectToXml("ElasticGrid", repository, "UTF-8");
        System.out.printf("XML is: %s\n", xml);
    }

    @Test
    public void testRoundtripOfRemoteStore() throws ObjectXmlMappingException, UnknownHostException {
        RemoteStore repository = new RemoteStore();
        repository.grid("test").grid("another grid");
        repository.application("test_application").application("another_fake_application");
        String xml = XmlUtils.convertObjectToXml("ElasticGrid", repository, "UTF-8");
        System.out.printf("XML is: %s\n", xml);
    }

    @Test
    public Grid testRoundtripOfEC2Grid() throws ObjectXmlMappingException, UnknownHostException {
        EC2Grid grid = (EC2Grid) gridFactory.createGrid().name("test-grid");

        // add nodes        
        Node node1 = grid.node("localhost", NodeProfile.MONITOR, InetAddress.getByName("localhost"));
        assertNotNull(node1);
        Node node2 = grid.node("blog.elastic-grid.com", NodeProfile.MONITOR,
                InetAddress.getByName("blog.elastic-grid.com"));
        assertNotNull(node2);

        // add applications
        Application application1 = grid.application("testApp1");
        application1.createNewOAR().name("oar1").version("1.0").opstring("opstring");
        assertNotNull(application1);
        Application application2 = grid.application("testApp2");
        application2.createNewOAR().name("oar2").version("1.0").opstring("opstring");
        assertNotNull(application2);

        String xml = XmlUtils.convertObjectToXml("ElasticGrid", grid, "UTF-8");
        assertNotNull(xml);
        System.out.printf("XML is: %s\n", xml);

        Grid roundtrip = XmlUtils.convertXmlToObject("ElasticGrid", EC2GridImpl.class, new StringReader(xml));
        assertNotNull(roundtrip);
        assertEquals(roundtrip.getName(), grid.getName());
        assertEquals(roundtrip.getNodes().size(), 2);
        assertEquals(roundtrip.getApplications().size(), 2);

        return grid;
    }

    @BeforeTest
    public void setupGridFactory() {
        gridFactory = new GridFactory() {
            public Grid createGrid() {
                return new EC2GridImpl();
            }
        };
    }

}
