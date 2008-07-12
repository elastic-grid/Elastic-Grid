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

package com.elasticgrid;

import com.xerox.amazonws.ec2.EC2Exception;
import com.xerox.amazonws.ec2.ImageDescription;
import com.xerox.amazonws.ec2.Jec2;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.io.*;

public class EC2Test {
    private Jec2 ec2;

    @Test
    public void testDescribeImages() throws EC2Exception {
        List<ImageDescription> imagesDescriptions = ec2.describeImages(Arrays.asList("ami-999174f0"));
        assert imagesDescriptions != null;
        assert imagesDescriptions.size() == 1;
        ImageDescription description = imagesDescriptions.get(0);
        assert description != null;
        System.out.printf("Description: %s %s %s %s\n",
                description.getImageLocation(),
                description.getImageOwnerId(),
                description.getImageState(),
                description.getProductCodes()
        );
    }

    @Test
    public void testDescribeImagesByOwner() throws EC2Exception {
        List<ImageDescription> imagesDescriptions = ec2.describeImagesByOwner(Arrays.asList("amazon"));
        assert imagesDescriptions != null;
        assert imagesDescriptions.size() > 1;
        for (ImageDescription description : imagesDescriptions) {
            assert description != null;
            System.out.printf("Description: %s %s %s %s\n",
                    description.getImageLocation(),
                    description.getImageOwnerId(),
                    description.getImageState(),
                    description.getProductCodes()
            );
        }
    }

    @BeforeClass
    public void setupEC2() throws IOException {
        String userHome = System.getProperty("user.home");
        File egUserDirectory = new File(userHome, ".eg");
        assert egUserDirectory.exists() : "The '$HOME/.eg' directory does not exists!";
        assert egUserDirectory.isDirectory() : "'$HOME/.eg' is not a directory!";
        Properties props = new Properties();
        InputStream propsStream = new FileInputStream(new File(egUserDirectory, "aws.properties"));
        try {
            props.load(propsStream);
            String accessId = (String) props.get("aws.accessId");
            assert accessId != null : "Empty AWS access ID!";
            String secretKey = (String) props.get("aws.secretKey");
            assert secretKey != null : "Empty AWS secret key!";
            ec2 = new Jec2(accessId, secretKey);
        } finally {
            propsStream.close();
        }
    }

    /*
    @BeforeClass
    public void setupEC2() throws Exception {
        service = new QueueServiceJSB();
        ServiceBeanContext context = new FakeServiceBeanContext();
        ((QueueServiceJSB) service).setServiceBeanContext(context);
        ((QueueServiceJSB) service).createProxy(service);
        ((QueueServiceJSB) service).postInitialize();
    }

    class FakeServiceBeanContext implements ServiceBeanContext {
        public Configuration getConfiguration() throws ConfigurationException {
            return new AbstractConfiguration() {
                protected Object getEntryInternal(String component, String name, Class type, Object data) throws ConfigurationException {
                    if ("awsAccessKeyId".equals(name))
                        return "0ZFE2GWP55PY92GD0A02";
                    else if ("awsSecretKey".equals(name))
                        return "Xj3hJngb3nJD5c3ti/O1MqW/A2v5gnd9ST6rns4h";
                    else if ("secured".equals(name))
                        return false;
                    else
                        return null;
                }
            };
        }
        public String getExportCodebase() { return null; }
        public ServiceBeanManager getServiceBeanManager() { return null; }
        public ComputeResourceManager getComputeResourceManager() { return null; }
        public Object getInitParameter(String s) { return null; }
        public Iterator<String> getInitParameterNames() { return null; }
        public ServiceElement getServiceElement() { return null; }
        public ServiceBeanConfig getServiceBeanConfig() { return null; }
        public DiscoveryManagement getDiscoveryManagement() throws IOException { return null; }
        public ComponentLoader getComponentLoader() { return null; }
        public AssociationManagement getAssociationManagement() { return null; }
        public WatchRegistry getWatchRegistry() { return null; }
    }
    */
}
