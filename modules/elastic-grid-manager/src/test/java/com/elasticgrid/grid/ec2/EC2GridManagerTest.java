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

package com.elasticgrid.grid.ec2;

import org.testng.annotations.Test;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import static org.easymock.EasyMock.*;
import com.elasticgrid.model.GridAlreadyRunningException;
import com.elasticgrid.model.GridException;
import com.elasticgrid.model.NodeProfile;
import com.elasticgrid.model.ec2.impl.EC2NodeImpl;
import com.elasticgrid.grid.NodeInstantiator;
import com.elasticgrid.grid.GridLocator;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class EC2GridManagerTest {
    private EC2GridManager gridManager;
    private NodeInstantiator mockEC2;
    private GridLocator mockLocator;

    @Test(expectedExceptions = GridAlreadyRunningException.class)
    public void testStartingARunningGrid() throws GridException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
        expect(mockLocator.findNodes("test"))
                .andReturn(null);
        expect(mockEC2.startInstances("", 1, 1, Arrays.asList("elastic-grid-cluster-test", "eg-monitor", "elastic-grid"),
                "MAX_MONITORS=3,YUM_PACKAGES=mencoder,AWS_ACCESS_ID=null,AWS_SECRET_KEY=null,AWS_SQS_SECURED=true",
                null, true, InstanceType.SMALL))
                .andReturn(null);
        expect(mockEC2.getGroupsNames())
                .andReturn(Arrays.asList("elastic-grid-cluster-test", "eg-monitor", "eg-agent", "elastic-grid"))
                .times(3);
        expect(mockLocator.findNodes("test"))
                .andReturn(Arrays.asList(new EC2NodeImpl(NodeProfile.MONITOR).instanceID("123")));
        replay(mockEC2, mockLocator);
        gridManager.startGrid("test");
        gridManager.startGrid("test");
    }

    @BeforeTest
    public void setUpGridManager() {
        gridManager = new EC2GridManager();
        mockEC2 = createMock(NodeInstantiator.class);
        mockLocator = createMock(GridLocator.class);
        gridManager.setNodeInstantiator(mockEC2);
        gridManager.setGridLocator(mockLocator);
    }

    @AfterMethod
    public void verifyMocks() {
        verify(mockEC2, mockLocator);
        reset(mockEC2, mockLocator);
    }
}
