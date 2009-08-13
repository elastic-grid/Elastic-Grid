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
package com.elasticgrid.storage.replicated;

import com.elasticgrid.config.RackspaceConfiguration;
import com.elasticgrid.storage.Container;
import com.elasticgrid.storage.ContainerNotFoundException;
import com.elasticgrid.storage.StorageException;
import com.elasticgrid.storage.rackspace.CloudFilesStorageEngine;
import com.elasticgrid.storage.spi.StorageEngine;
import com.elasticgrid.utils.rackspace.RackspaceUtils;
import org.apache.commons.lang.math.RandomUtils;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.testng.annotations.Test;
import java.rmi.RemoteException;
import java.util.Properties;

/**
 * {@link ReplicatedStorageEngine} tests.
 *
 * @author Jerome Bernard
 */
public class ReplicatedStorageEngineWithFailingPreferredStorageEngineTest {

    @Test(expectedExceptions = StorageException.class)
    public void testCreationOfContainers() throws StorageException, RemoteException {
        StorageEngine mgr = getStorageEngine();
        // create a container
        String containerName = getTestContainer();
        Container container = mgr.createContainer(containerName);
    }

    @Test(expectedExceptions = {ContainerNotFoundException.class})
    public void testFindUnknownContainer() throws StorageException, RemoteException {
        StorageEngine mgr = getStorageEngine();
        mgr.findContainerByName("whatever-" + RandomUtils.nextInt());
    }

    @Test(expectedExceptions = {StorageException.class})
    public void testDeletionOfUnknownContainer() throws StorageException, RemoteException {
        StorageEngine mgr = getStorageEngine();
        mgr.deleteContainer("whatever-" + RandomUtils.nextInt());
    }

    protected String getTestContainer() {
        return "test-" + RandomUtils.nextInt();
    }

    protected StorageEngine getStorageEngine() throws StorageException, RemoteException {
        return new ReplicatedStorageEngine(
                createAmazonStorageEngine(),
                createRackspaceStorageEngine()
        );
    }

    private StorageEngine createRackspaceStorageEngine() throws StorageException {
        try {
            Properties rackspaceConfig = RackspaceUtils.loadRackspaceConfiguration();
            String login = rackspaceConfig.getProperty(RackspaceConfiguration.LOGIN);
            String password = rackspaceConfig.getProperty(RackspaceConfiguration.API_KEY);
            if (login == null) {
                throw new IllegalArgumentException("Could not find Cloud Files login");
            }
            if (password == null) {
                throw new IllegalArgumentException("Could not find Cloud Files password");
            }
            return new CloudFilesStorageEngine(login, password);
        } catch (Exception e) {
            throw new StorageException("Can't initialize Cloud Files Storage Manager", e);
        }
    }

    private StorageEngine createAmazonStorageEngine() throws StorageException, RemoteException {
        StorageEngine mock = mock(StorageEngine.class);
        when(mock.getStorageName()).thenReturn("Mocked Storage Engine");
        StorageException storageException = new StorageException("Simulating storage engine failure");
        doThrow(storageException).when(mock).deleteContainer(anyString());
        doThrow(storageException).when(mock).findContainerByName(anyString());
        doThrow(storageException).when(mock).createContainer(anyString());
        return mock;
//        try {
//            Properties awsConfig = AWSUtils.loadEC2Configuration();
//            String awsAccessID = awsConfig.getProperty(EC2Configuration.AWS_ACCESS_ID);
//            String awsSecretKey = awsConfig.getProperty(EC2Configuration.AWS_SECRET_KEY);
//            if (awsAccessID == null) {
//                throw new IllegalArgumentException("Could not find AWS Access ID");
//            }
//            if (awsSecretKey == null) {
//                throw new IllegalArgumentException("Could not find AWS Secret Key");
//            }
//            return new S3StorageEngine(awsAccessID, awsSecretKey);
//        } catch (Exception e) {
//            throw new StorageException("Can't initialize S3 Storage Manager", e);
//        }
    }
}