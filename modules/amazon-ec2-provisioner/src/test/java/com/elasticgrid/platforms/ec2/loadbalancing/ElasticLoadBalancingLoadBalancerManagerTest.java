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

package com.elasticgrid.platforms.ec2.loadbalancing;

import com.elasticgrid.loadbalancing.LoadBalancerManager;
import com.elasticgrid.loadbalancing.LoadBalancingException;
import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.Listener;
import com.xerox.amazonws.ec2.LoadBalancing;
import org.easymock.EasyMock;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.expect;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.reset;
import static org.easymock.classextension.EasyMock.verify;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;

public class ElasticLoadBalancingLoadBalancerManagerTest {
    private LoadBalancerManager lbm;
    private LoadBalancing elb;
    private Jec2 ec2;

    @Test
    public void testCreationOfLoadBalancer() throws LoadBalancingException, RemoteException, com.xerox.amazonws.ec2.LoadBalancingException {
        expect(elb.createLoadBalancer("test", EasyMock.<List<Listener>>anyObject(), Collections.singletonList((String) null), "??"))
                .andReturn("the-dns-information.mydomain.com");
        replay(elb);
        replay(ec2);
        ElasticLoadBalancingLoadBalancer lb = (ElasticLoadBalancingLoadBalancer) lbm.createLoadBalancer("test", 80, 8080, "HTTP");
        assert "the-dns-information.mydomain.com".equals(lb.getDnsName());
    }

//    @Test(expectedExceptions = ClusterAlreadyRunningException.class)
//    public void testStartingARunningGrid() throws ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
//        mockEC2 = EasyMock.createMock(EC2Instantiator.class);
//        EasyMock.expect(mockEC2.startInstances(egProps.getProperty(EC2Configuration.AWS_EC2_AMI32),
//                1, 1, Arrays.asList("elastic-grid-cluster-test", "eg-monitor", "eg-agent", "elastic-grid"),
//                "CLUSTER_NAME=test,AWS_ACCESS_ID=null,AWS_SECRET_KEY=null," +
//                        "AWS_EC2_AMI32=" + egProps.getProperty(EC2Configuration.AWS_EC2_AMI32) + "," +
//                        "AWS_EC2_AMI64=" + egProps.getProperty(EC2Configuration.AWS_EC2_AMI64) + "," +
//                        "AWS_EC2_KEYPAIR=" + egProps.getProperty(EC2Configuration.AWS_EC2_KEYPAIR) + "," +
//                        "AWS_SQS_SECURED=true,DROP_BUCKET=" + egProps.getProperty(EC2Configuration.EG_DROP_BUCKET),
//                egProps.getProperty(EC2Configuration.AWS_EC2_KEYPAIR), true, EC2NodeType.SMALL))
//                .andReturn(null);
//        EasyMock.expect(mockEC2.getGroupsNames())
//                .andReturn(Arrays.asList("elastic-grid-cluster-test", "eg-monitor", "eg-agent", "elastic-grid"))
//                .anyTimes();
//        mockLocator = org.easymock.classextension.EasyMock.createMock(EC2ClusterLocator.class);
//        EasyMock.expect(mockLocator.findNodes("test"))
//                        .andReturn(null);
//        EasyMock.expect(mockLocator.findNodes("test"))
//                .andReturn(new HashSet<EC2Node>(Arrays.asList(new EC2NodeImpl(NodeProfile.MONITOR_AND_AGENT, EC2NodeType.SMALL).instanceID("123"))));
//        EasyMock.replay(mockEC2);
//        org.easymock.classextension.EasyMock.replay(mockLocator);
//
//        cloudPlatformManager.setNodeInstantiator(mockEC2);
//        cloudPlatformManager.setClusterLocator(mockLocator);
//
//        NodeProfileInfo monitorAndAgentSmall = new NodeProfileInfo(NodeProfile.MONITOR_AND_AGENT, EC2NodeType.SMALL, 1);
//        cloudPlatformManager.startCluster("test", Arrays.asList(monitorAndAgentSmall));
//        cloudPlatformManager.startCluster("test", Arrays.asList(monitorAndAgentSmall));
//    }

    @BeforeTest
    @SuppressWarnings("unchecked")
    public void setUpLoadBalancerManager() throws IOException {
        elb = createMock(LoadBalancing.class);
        ec2 = createMock(Jec2.class);
        lbm = new ElasticLoadBalancingLoadBalancerManager(elb, ec2);
    }

    @AfterTest
    public void verifyMocks() {
        verify(elb);
        reset(elb);
        verify(ec2);
        reset(ec2);
    }
}