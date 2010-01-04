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
package com.elasticgrid.storage.spi;

import com.elasticgrid.storage.Container;
import com.elasticgrid.storage.ContainerNotFoundException;
import com.elasticgrid.storage.Storable;
import com.elasticgrid.storage.StorableNotFoundException;
import com.elasticgrid.storage.StorageException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.testng.annotations.Test;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.rmi.RemoteException;

/**
 * Helper class providing support for writing {@link com.elasticgrid.storage.StorageManager} implementations tests.
 *
 * @author Jerome Bernard
 */
public abstract class AbstractStorageEngineTest {

    @Test
    public void testCreationOfContainers() throws StorageException, RemoteException {
        StorageEngine mgr = getStorageEngine();
        // create a container
        String containerName = getTestContainer();
        Container container = mgr.createContainer(containerName);
        assert container != null;
        // ensure it's available now in the list of containers
        List<Container> containers = mgr.getContainers();
        boolean found = false;
        for (Container c : containers)
            if (c.getName().equals(containerName))
                found = true;
        assert found : "Can't find container " + containerName;
        // try to locate it by name
        container = mgr.findContainerByName(containerName);
        assert container != null;
        // delete the container
        mgr.deleteContainer(containerName);
        // ensure it's gone in the list of containers
        containers = mgr.getContainers();
        found = false;
        for (Container c : containers)
            if (c.getName().equals(containerName))
                found = true;
        assert !found;
    }

    @Test(expectedExceptions = {ContainerNotFoundException.class})
    public void testFindUnknownContainer() throws StorageException, RemoteException {
        StorageEngine mgr = getStorageEngine();
        mgr.findContainerByName("whatever-" + RandomUtils.nextInt());
    }

    @Test(expectedExceptions = {ContainerNotFoundException.class})
    public void testDeletionOfUnknowContainer() throws StorageException, RemoteException {
        StorageEngine mgr = getStorageEngine();
        mgr.deleteContainer("whatever-" + RandomUtils.nextInt());
    }

    @Test
    public void testStorablesInContainer() throws StorageException, IOException {
        StorageEngine mgr = getStorageEngine();
        // create a container
        String containerName = getTestContainer();
        Container container = mgr.createContainer(containerName);
        assert container != null;
        assert container.listStorables().size() == 0;
        // create a new storable in that container
        File file = new File(System.getProperty("java.io.tmp^dir"), "filename.txt");
        FileUtils.writeStringToFile(file, "Elastic Grid: some dummy content");
        Storable s = container.uploadStorable("directory/subdirectory/filename.txt", file);
        assert s != null;
        assert "directory/subdirectory/filename.txt".equals(s.getName());
        // check that it's available in the list of storables in that container with the two "directories"
        assert container.listStorables().size() == 1;
        List<Storable> storables = container.listStorables();
        s = storables.get(0);
        assert "directory/subdirectory/filename.txt".equals(s.getName());
        // check that it can be retreive by name/key
        s = container.findStorableByName("directory/subdirectory/filename.txt");
        assert s != null;
        assert "directory/subdirectory/filename.txt".equals(s.getName());
        // delete the container
        mgr.deleteContainer(containerName);
    }

    @Test(expectedExceptions = {StorableNotFoundException.class})
    public void testStorablesNotFound() throws StorageException, IOException {
        StorageEngine mgr = getStorageEngine();
        String containerName = getTestContainer();
        try {
            // create a container
            Container container = mgr.createContainer(containerName);
            assert container != null;
            assert container.listStorables().size() == 0;
            // try to retrieve a storage which does not exists
            container.findStorableByName("whatever");
        } finally {
            // delete the container
            mgr.deleteContainer(containerName);
        }
    }

    protected String getTestContainer() {
        return "test-" + RandomUtils.nextInt();
    }

    protected abstract StorageEngine getStorageEngine() throws StorageException;

}
