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
package com.elasticgrid.storage;

import com.elasticgrid.storage.spi.StorageEngine;
import net.jini.core.lookup.ServiceItem;
import org.rioproject.associations.Association;
import org.rioproject.core.jsb.ServiceBeanContext;
import org.rioproject.jsb.ServiceBeanAdapter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * JSB exposing the {@link com.elasticgrid.cluster.ClusterManager}.
 *
 * @author Jerome Bernard
 */
public class StorageManagerJSB extends ServiceBeanAdapter implements StorageManager {
    private StorageEngine preferredStorageEngine;
    private List<StorageEngine> storageEngines = new ArrayList<StorageEngine>();
    private static final Logger logger = Logger.getLogger("com.elasticgrid.cluster");

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(ServiceBeanContext context) throws Exception {
        super.initialize(context);
    }

    @Override
    public void advertise() throws IOException {
        super.advertise();
        logger.info("Advertised Storage Manager");
    }

    public StorageEngine getPreferredStorageEngine() throws StorageException, RemoteException {
        return preferredStorageEngine;
    }

    public List<StorageEngine> getAvailableStorageEngines() throws StorageException, RemoteException {
        return storageEngines;
    }

    @SuppressWarnings("unchecked")
    public void setStorageEngines(Association<StorageEngine> association) throws RemoteException {
        storageEngines.clear();
        for (ServiceItem serviceItem : association.getServiceItems()) {
            StorageEngine engine = (StorageEngine) serviceItem.service;
            storageEngines.add(engine);
        }
        if (storageEngines.isEmpty())
            preferredStorageEngine = null;
        if (storageEngines.size() == 1)
            preferredStorageEngine = storageEngines.get(0);
        else
            preferredStorageEngine = storageEngines.get(0); // TODO: write something way more smarter!!
    }

}