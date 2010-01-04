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
package com.elasticgrid.admin.server;

import com.elasticgrid.admin.client.StorageManagerService;
import com.elasticgrid.admin.model.Container;
import com.elasticgrid.admin.model.Storable;
import com.elasticgrid.admin.model.StorageEngine;
import com.elasticgrid.storage.StorageException;
import com.elasticgrid.storage.StorageManager;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.rioproject.associations.AssociationDescriptor;
import org.rioproject.associations.AssociationMgmt;
import org.rioproject.associations.AssociationType;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StorageManagerServiceImpl extends RemoteServiceServlet implements StorageManagerService {
    private static StorageManager storageManager;
    private static final Logger logger = Logger.getLogger(StorageManagerServiceImpl.class.getName());

    public StorageManagerServiceImpl() {
        AssociationDescriptor storageManagerAssociation = new AssociationDescriptor(AssociationType.REQUIRES);
        storageManagerAssociation.setMatchOnName(false);
        storageManagerAssociation.setInterfaceNames(StorageManager.class.getName());
        storageManagerAssociation.setGroups(System.getProperty("org.rioproject.groups"));
        AssociationMgmt assocMgt = new AssociationMgmt();
        assocMgt.register(new StorageManagerListener());
        assocMgt.addAssociationDescriptors(storageManagerAssociation);
    }

    public List<StorageEngine> getAvailableStorageEngines() {
        try {
            List<com.elasticgrid.storage.spi.StorageEngine> raw = getStorageManager().getAvailableStorageEngines();
            ArrayList<StorageEngine> engines = new ArrayList<StorageEngine>(raw.size());
            for (com.elasticgrid.storage.spi.StorageEngine engine : raw) {
                // add cluster to model
                engines.add(buildStorageEngine(engine, false));
            }
            return engines;
        } catch (Exception e) {
            throw new IllegalStateException("Can't fetch storage engines", e);
        }
    }

    public StorageEngine getStorageEngineDetails(String name) {
        try {
            List<com.elasticgrid.storage.spi.StorageEngine> engines = getStorageManager().getAvailableStorageEngines();
            for (com.elasticgrid.storage.spi.StorageEngine e : engines)
                if (name.equals(e.getStorageName()))
                    return buildStorageEngine(e, true);
            return null;
        } catch (Exception e) {
            throw new IllegalStateException("Can't fetch list of containers", e);
        }
    }

    private StorageEngine buildStorageEngine(com.elasticgrid.storage.spi.StorageEngine engine, boolean includeStorables) throws RemoteException, StorageException {
        List<com.elasticgrid.storage.Container> raw = engine.getContainers();
        List<Container> containers = new ArrayList<Container>(raw.size());
        for (com.elasticgrid.storage.Container container : raw) {
            Container c = null;
            if (includeStorables) {
                // add container's storables
                List<com.elasticgrid.storage.Storable> rawStorables = container.listStorables();
                List<Storable> storables = new ArrayList<Storable>(rawStorables.size());
                for (com.elasticgrid.storage.Storable storable : rawStorables)
                    storables.add(new Storable(storable.getName(), storable.getLastModifiedDate()));
                c = new Container(container.getName(), storables);
            } else {
                c = new Container(container.getName());
            }
            // add container
            containers.add(c);
        }
        return new StorageEngine(engine.getStorageName(), containers);
    }

    public synchronized static StorageManager getStorageManager() throws InterruptedException {
        while (storageManager == null) {
            logger.warning("Waiting for discovery of storage manager...");
            Thread.sleep(200);
        }
        return storageManager;
    }

    public static void setStorageManager(StorageManager storageManager) {
        logger.log(Level.FINE, "Found storage manager {0}", storageManager.getClass().getName());
        StorageManagerServiceImpl.storageManager = storageManager;
    }
}
