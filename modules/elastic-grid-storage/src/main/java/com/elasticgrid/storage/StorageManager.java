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

import java.util.List;

/**
 * The Storage Manager enables virtualization of underlying storage providers.
 * @author Jerome Bernard
 */
public interface StorageManager {

    /**
     * Return the list of containers:
     * <ul>
     *   <li>Amazon: similar to S3 list of buckets</li>
     *   <li>Eucalyptus: similar to S3-like list of buckets</li>
     *   <li>Mosso: similar to containers of Cloud Files</li>
     * </ul>
     * @return the list of containers
     * @throws StorageException if the list of containers can't be retrieved
     */
    List<Container> getContainers() throws StorageException;

    /**
     * Create a new container.
     * @param name the name of the container to create
     * @return the created container
     * @throws StorageException if the container can't be created
     */
    Container createContainer(String name) throws StorageException;

}
