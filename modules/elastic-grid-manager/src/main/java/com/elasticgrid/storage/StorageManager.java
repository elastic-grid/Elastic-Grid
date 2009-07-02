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

import com.elasticgrid.storage.spi.StorageEngine;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * The Storage Manager enables virtualization of underlying storage providers.
 * @author Jerome Bernard
 */
public interface StorageManager extends Remote {

    /**
     * Return the preferred {@link StorageEngine} which should be used by default unless there is a
     * need for a specific storage engine.
     * The preferred storage engine is usually different depending on the underlying Cloud Platform
     * where Elastic Grid is running.
     * @return the preferred storage engine
     * @throws StorageException
     * @throws RemoteException
     */
    StorageEngine getPreferredStorageEngine() throws StorageException, RemoteException;

    /**
     * Return the list of available {@link StorageEngine}s based on their availability within the
     * cluster.
     * @return the available storage engines
     * @throws StorageException
     * @throws RemoteException
     */
    List<StorageEngine> getAvailableStorageEngines() throws StorageException, RemoteException;

}
