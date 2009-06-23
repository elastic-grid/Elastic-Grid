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
package com.elasticgrid.storage.mosso.cloudfiles;

import com.elasticgrid.storage.Container;
import com.elasticgrid.storage.StorageException;
import com.elasticgrid.storage.StorageManager;
import com.elasticgrid.storage.ContainerNotFoundException;
import com.mosso.client.cloudfiles.FilesClient;
import com.mosso.client.cloudfiles.FilesContainer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@link StorageManager} providing support for Mosso Cloud Files.
 *
 * @author Jerome Bernard
 */
public class MossoStorageManager implements StorageManager {
    private FilesClient mosso;
    private final Logger logger = Logger.getLogger(MossoStorageManager.class.getName());

    public MossoStorageManager(String login, String password) {
        mosso = new FilesClient(login, password);
    }

    public List<Container> getContainers() throws StorageException {
        try {
            logger.log(Level.FINE, "Retrieving list of Cloud Files containers");
            mosso.login();
            List<FilesContainer> mossoContainers = mosso.listContainers();
            List<Container> containers = new ArrayList<Container>(mossoContainers.size());
            for (FilesContainer mossoContainer : mossoContainers) {
                containers.add(new MossoContainer(mosso, mossoContainer));
            }
            return containers;
        } catch (Exception e) {
            throw new StorageException("Can't get list of containers", e);
        }
    }

    public Container createContainer(String name) throws StorageException {
        try {
            logger.log(Level.FINE, "Creating Clouds File container {0}", name);
            mosso.login();
            mosso.createContainer(name);
            FilesContainer mossoContainer = null;
            List<FilesContainer> containers = mosso.listContainers();
            for (FilesContainer container : containers)
                if (container.getName().equals(name))
                    mossoContainer = container;
            return new MossoContainer(mosso, mossoContainer);
        } catch (Exception e) {
            throw new StorageException("Can't create container", e);
        }
    }

    public void deleteContainer(String name) throws StorageException {
        try {
            logger.log(Level.FINE, "Deleting Cloud Files container {0}", name);
            mosso.login();
            if (!mosso.deleteContainer(name))
                throw new ContainerNotFoundException(name);
        } catch (Exception e) {
            throw new StorageException("Can't delete container", e);
        }
    }
}
