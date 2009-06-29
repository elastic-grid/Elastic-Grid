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
package com.elasticgrid.storage.rackspace;

import com.elasticgrid.storage.Container;
import com.elasticgrid.storage.StorageException;
import com.elasticgrid.storage.StorageManager;
import com.elasticgrid.storage.ContainerNotFoundException;
import com.mosso.client.cloudfiles.FilesClient;
import com.mosso.client.cloudfiles.FilesContainer;
import com.mosso.client.cloudfiles.FilesNotFoundException;
import com.mosso.client.cloudfiles.FilesObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;

/**
 * {@link StorageManager} providing support for Rackspace Cloud Files.
 *
 * @author Jerome Bernard
 */
public class CloudFilesStorageManager implements StorageManager {
    private FilesClient rackspace;
    private final Logger logger = Logger.getLogger(CloudFilesStorageManager.class.getName());

    public CloudFilesStorageManager(String login, String apiKey) throws IOException {
        rackspace = new FilesClient(login, apiKey);
    }

    public List<Container> getContainers() throws StorageException {
        try {
            logger.log(Level.FINE, "Retrieving list of Cloud Files containers");
            rackspace.login();
            List<FilesContainer> rackspaceContainers = rackspace.listContainers();
            List<Container> containers = new ArrayList<Container>(rackspaceContainers.size());
            for (FilesContainer rackspaceContainer : rackspaceContainers) {
                containers.add(new CloudFilesContainer(rackspace, rackspaceContainer));
            }
            return containers;
        } catch (Exception e) {
            throw new StorageException("Can't get list of containers", e);
        }
    }

    public Container createContainer(String name) throws StorageException {
        try {
            logger.log(Level.FINE, "Creating Clouds File container {0}", name);
            rackspace.login();
            rackspace.createContainer(name);
            FilesContainer rackspaceContainer = null;
            List<FilesContainer> containers = rackspace.listContainers();
            for (FilesContainer container : containers)
                if (container.getName().equals(name))
                    rackspaceContainer = container;
            return new CloudFilesContainer(rackspace, rackspaceContainer);
        } catch (Exception e) {
            throw new StorageException("Can't create container", e);
        }
    }

    public void deleteContainer(String name) throws StorageException {
        try {
            logger.log(Level.FINE, "Deleting Cloud Files container {0}", name);
            rackspace.login();
            for (FilesObject o : rackspace.listObjects(name))
                rackspace.deleteObject(name, o.getName());
            rackspace.deleteContainer(name);
        } catch (FilesNotFoundException e) {
            throw new ContainerNotFoundException(name);
        } catch (Exception e) {
            throw new StorageException("Can't delete container", e);
        }
    }
}
