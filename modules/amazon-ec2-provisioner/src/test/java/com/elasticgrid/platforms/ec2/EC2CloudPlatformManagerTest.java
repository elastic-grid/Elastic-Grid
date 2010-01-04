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
package com.elasticgrid.platforms.ec2;

import com.elasticgrid.config.EC2Configuration;
import com.elasticgrid.model.ClusterAlreadyRunningException;
import com.elasticgrid.model.ClusterException;
import com.elasticgrid.model.NodeProfile;
import com.elasticgrid.model.NodeProfileInfo;
import com.elasticgrid.model.ec2.EC2Node;
import com.elasticgrid.model.ec2.EC2NodeType;
import com.elasticgrid.model.ec2.impl.EC2NodeImpl;
import com.elasticgrid.platforms.ec2.discovery.EC2ClusterLocator;
import com.elasticgrid.utils.amazon.AWSUtils;
import static org.mockito.Mockito.*;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class EC2CloudPlatformManagerTest {
    private EC2CloudPlatformManager cloudPlatformManager;
    private EC2NodeInstantiator mockEC2Node;
    private EC2ClusterLocator mockLocator;
    private Properties egProps;

    @Test(expectedExceptions = ClusterAlreadyRunningException.class)
    public void testStartingARunningGrid() throws ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
        mockEC2Node = mock(EC2NodeInstantiator.class);
        when(mockEC2Node.startInstances(egProps.getProperty(EC2Configuration.AWS_EC2_AMI32),
                1, 1, Arrays.asList("elastic-grid-cluster-test", "eg-monitor", "eg-agent", "elastic-grid"),
                "CLUSTER_NAME=test,AWS_ACCESS_ID=null,AWS_SECRET_KEY=null," +
                        "AWS_EC2_AMI32=" + egProps.getProperty(EC2Configuration.AWS_EC2_AMI32) + "," +
                        "AWS_EC2_AMI64=" + egProps.getProperty(EC2Configuration.AWS_EC2_AMI64) + "," +
                        "AWS_EC2_KEYPAIR=" + egProps.getProperty(EC2Configuration.AWS_EC2_KEYPAIR) + "," +
                        "AWS_SQS_SECURED=true,DROP_BUCKET=" + egProps.getProperty(EC2Configuration.EG_DROP_BUCKET),
                egProps.getProperty(EC2Configuration.AWS_EC2_KEYPAIR), true, EC2NodeType.SMALL))
                .thenReturn(null);
        when(mockEC2Node.getGroupsNames())
                .thenReturn(Arrays.asList("elastic-grid-cluster-test", "eg-monitor", "eg-agent", "elastic-grid"));
        mockLocator = mock(EC2ClusterLocator.class);
        when(mockLocator.findNodes("test"))
                        .thenReturn(null);
        when(mockLocator.findNodes("test"))
                .thenReturn(new HashSet<EC2Node>(Arrays.asList(new EC2NodeImpl(NodeProfile.MONITOR_AND_AGENT, EC2NodeType.SMALL).instanceID("123"))));

        cloudPlatformManager.setNodeInstantiator(mockEC2Node);
        cloudPlatformManager.setClusterLocator(mockLocator);
        
        NodeProfileInfo monitorAndAgentSmall = new NodeProfileInfo(NodeProfile.MONITOR_AND_AGENT, EC2NodeType.SMALL, 1);
        cloudPlatformManager.startCluster("test", Arrays.asList(monitorAndAgentSmall));
        cloudPlatformManager.startCluster("test", Arrays.asList(monitorAndAgentSmall));

        verify(mockEC2Node);
        verify(mockLocator);
    }

    @BeforeTest
    @SuppressWarnings("unchecked")
    public void setUpClusterManager() throws IOException {
        Properties config = AWSUtils.loadEC2Configuration();
        cloudPlatformManager = new EC2CloudPlatformManager();
        cloudPlatformManager.setAmi32(config.getProperty(EC2Configuration.AWS_EC2_AMI32));
        cloudPlatformManager.setAmi64(config.getProperty(EC2Configuration.AWS_EC2_AMI64));
        egProps = AWSUtils.loadEC2Configuration();
    }

}
