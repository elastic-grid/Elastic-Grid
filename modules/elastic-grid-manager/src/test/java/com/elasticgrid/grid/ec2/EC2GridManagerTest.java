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

import org.springframework.test.context.ContextConfiguration;
import com.elasticgrid.grid.GridManager;
import com.elasticgrid.grid.ec2.impl.EC2GridFactory;
import com.elasticgrid.model.Grid;
import com.elasticgrid.model.GridFactory;
import com.elasticgrid.repository.RepositoryManager;
import com.elasticgrid.amazon.ec2.EC2Instantiator;
import com.elasticgrid.amazon.ec2.InstanceType;
import static org.easymock.EasyMock.*;
import org.testng.annotations.Test;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;

import java.rmi.RemoteException;
import java.util.Collections;

@ContextConfiguration(locations = "/com/elasticgrid/grid/ec2/applicationContext.xml")
public class EC2GridManagerTest {
    private GridManager gridManager;
    private GridFactory gridFactory;
    private RepositoryManager mockRepository;
    private EC2Instantiator mockEC2;

    @Test
    public void testLifecycle() throws RemoteException, GridNotFoundException, GridAlreadyRunningException {
        expect(mockEC2.startInstances(null, 1, 1, Collections.<String>emptyList(), "", null, true, InstanceType.SMALL))
                .andReturn(null);
        expect(mockRepository.grid("test")).andReturn(null);
        expect(mockRepository.grid("test")).andReturn(gridFactory.createGrid().name("test").status(Grid.Status.RUNNING));
//        mockEC2.shutdownInstance(null);
        mockRepository.destroyGrid("test");
        replay(mockEC2, mockRepository);
        gridManager.startGrid("test");
        gridManager.destroyGrid("test");
    }

    @Test(expectedExceptions = GridAlreadyRunningException.class)
    public void testStartingARunningGrid() throws GridAlreadyRunningException, RemoteException {
        expect(mockEC2.startInstances(null, 1, 1, Collections.<String>emptyList(), "", null, true, InstanceType.SMALL))
                .andReturn(null);
        expect(mockRepository.grid("test")).andReturn(null);
        expect(mockRepository.grid("test")).andReturn(gridFactory.createGrid().name("test").status(Grid.Status.RUNNING));
        replay(mockEC2, mockRepository);
        gridManager.startGrid("test");
        gridManager.startGrid("test");
    }

    @Test(expectedExceptions = GridNotFoundException.class)
    public void testDestroyUnknownGrid() throws GridNotFoundException, RemoteException {
        expect(mockRepository.grid("test")).andReturn(null);        
        replay(mockEC2, mockRepository);
        gridManager.destroyGrid("test");
    }

    @BeforeTest
    public void setUpGridManager() {
        gridManager = new EC2GridManager();
        gridFactory = new EC2GridFactory();
        mockEC2 = createMock(EC2Instantiator.class);
        mockRepository = createMock(RepositoryManager.class);
        ((EC2GridManager) gridManager).setEc2(mockEC2);
        ((EC2GridManager) gridManager).setRepositoryManager(mockRepository);
    }

    @AfterMethod
    public void verifyMocks() {
        verify(mockEC2, mockRepository);
        reset(mockEC2, mockRepository);
    }
}
