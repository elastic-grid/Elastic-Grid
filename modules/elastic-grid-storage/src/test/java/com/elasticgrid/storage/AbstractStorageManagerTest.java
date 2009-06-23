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
package com.elasticgrid.storage;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;
import java.util.List;
import java.io.File;
import java.io.IOException;

/**
 * Helper class providing support for writing {@link StorageManager} implementations tests.
 *
 * @author Jerome Bernard
 */
public abstract class AbstractStorageManagerTest {

    @Test
    public void testCreationOfContainers() throws StorageException {
        StorageManager mgr = getStorageManager();
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
    public void testDeletionOfUnknowContainer() throws StorageException {
        StorageManager mgr = getStorageManager();
        mgr.deleteContainer("whatever-" + RandomUtils.nextInt());
    }

    @Test
    public void testStorablesInContainer() throws StorageException, IOException {
        StorageManager mgr = getStorageManager();
        // create a container
        String containerName = getTestContainer();
        Container container = mgr.createContainer(containerName);
        assert container != null;
        assert container.listStorables().size() == 0;
        // create a new storable in that container
        File file = File.createTempFile("test", "eg");
        FileUtils.writeStringToFile(file, "some dummy content");
        Storable s = container.uploadStorable("directory/subdirectory/filename.ext", file);
        assert s != null;
        assert "directory/subdirectory/filename.ext".equals(s.getName());
        // check that it's available in the list of storables in that container
        assert container.listStorables().size() == 1;
        List<Storable> storables = container.listStorables();
        s = storables.get(0);
        assert "directory/subdirectory/filename.ext".equals(s.getName());
        // check that it can be retreive by name/key
        s = container.findStorableByName("directory/subdirectory/filename.ext");
        assert s != null;
        assert "directory/subdirectory/filename.ext".equals(s.getName());
        // delete the container
        mgr.deleteContainer(containerName);
    }

    @Test(expectedExceptions = {StorableNotFoundException.class})
    public void testStorablesNotFound() throws StorageException, IOException {
        StorageManager mgr = getStorageManager();
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

    protected abstract StorageManager getStorageManager() throws StorageException;

}
