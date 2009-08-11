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

import com.elasticgrid.storage.Container;
import com.elasticgrid.storage.ContainerNotFoundException;
import com.elasticgrid.storage.StorageException;
import com.elasticgrid.storage.spi.StorageEngine;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Replicated {@link StorageEngine} ensuring that many {@link StorageEngine}s are kept in sync each time a write
 * operation is done. All read operations are done against a preferred {@link StorageEngine}
 *
 * @author Jerome Bernard
 */
public class ReplicatedStorageEngine implements StorageEngine {
    private final StorageEngine preferred;
    private final List<StorageEngine> otherEngines;
    private static final Logger logger = Logger.getLogger(ReplicatedStorageEngine.class.getName());

    public ReplicatedStorageEngine(StorageEngine preferred, StorageEngine... otherEngines) {
        this.preferred = preferred;
        this.otherEngines = Arrays.asList(otherEngines);
    }

    public String getStorageName() throws RemoteException {
        return "Replicated Storage Engine";
    }

    public List<Container> getContainers() throws StorageException, RemoteException {
        try {
//            List<Container> otherContainers = new LinkedList<Container>();
//            for (StorageEngine engine : otherEngines) {
//                try {
//                    otherContainers.addAll(engine.getContainers());
//                } catch (StorageException e) {
//                    logger.log(Level.SEVERE,
//                            "Could not get containers of storage engine {0}. {0} won't be in sync anymore...",
//                            engine.getStorageName());
//                }
//            }
//            return new ReplicatedContainer(preferred.getContainers(), otherContainers);
            return preferred.getContainers();
        } catch (SecurityException e) {
            logger.log(Level.WARNING,
                    "Could not find containers via {1}. Trying next storage engine...",
                    new Object[]{preferred.getStorageName()});
            for (StorageEngine engine : otherEngines) {
                try {
                    return engine.getContainers();
                } catch (StorageException e1) {
                    logger.log(Level.WARNING,
                            "Could not find containers via {1}. Trying next storage engine...",
                            new Object[]{engine.getStorageName()});
                }
            }
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     * If the container can't be created on all storage engines, a best effort is done in order to delete
     * created containers that shouldn't be there because of the <em>global</em> rollback.
     *
     * @param name the name of the container to create
     * @return the created container
     * @throws StorageException
     * @throws RemoteException
     */
    public Container createContainer(String name) throws StorageException, RemoteException {
        Container container = preferred.createContainer(name);
        for (int i = 0; i < otherEngines.size(); i++) {
            StorageEngine engine = otherEngines.get(i);
            try {
                return new ReplicatedContainer(engine.createContainer(name), null);     // TODO: add replicated containers
            } catch (StorageException e) {
                logger.log(Level.WARNING,
                        "Could not create container ''{0}'' on storage engine {1}. Trying to revert to shared state...",
                        new Object[]{name, engine.getStorageName()});
                logger.log(Level.FINE, "Can't create container '" + name + "'", e);
                try {
                    for (int j = 0; j < i; j++)
                        otherEngines.get(j).deleteContainer(name);
                    preferred.deleteContainer(name);
                } catch (StorageException e1) {
                    logger.log(Level.SEVERE,
                            "Could not revert to shared state. Container ''{0}'' could not be deleted on storage engine '{1}'!",
                            new Object[]{name, preferred.getStorageName()});
                    throw e;
                }
            }
        }
        return container;
    }

    public Container findContainerByName(String name) throws StorageException, RemoteException {
        try {
            return new ReplicatedContainer(preferred.findContainerByName(name), null);  // TODO: add replicated containers
        } catch (StorageException e) {
            logger.log(Level.WARNING,
                    "Could not find container ''{0}'' via {1}. Trying next storage engine...",
                    new Object[]{name, preferred.getStorageName()});
            for (StorageEngine engine : otherEngines) {
                try {
                    return engine.findContainerByName(name);
                } catch (ContainerNotFoundException e1) {
                    throw e1;
                } catch (StorageException e1) {
                    logger.log(Level.WARNING,
                            "Could not find container ''{0}'' via {1}. Trying next storage engine...",
                            new Object[]{name, engine.getStorageName()});
                }
            }
            throw e;
        }
    }

    public void deleteContainer(String name) throws StorageException, RemoteException {
        preferred.deleteContainer(name);
        for (StorageEngine engine : otherEngines) {
            try {
                engine.deleteContainer(name);
            } catch (StorageException e) {
                logger.log(Level.SEVERE, "Could not delete container ''{0}'' on storage engine {1}. Do it manually if needed!",
                        new Object[] { name, engine.getStorageName() });
            }
        }
    }

}
