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
package com.elasticgrid.storage.rackspace;

import com.elasticgrid.storage.Container;
import com.elasticgrid.storage.StorageException;
import com.elasticgrid.storage.spi.StorageEngine;
import com.elasticgrid.utils.rackspace.RackspaceUtils;
import org.rioproject.core.jsb.ServiceBeanContext;
import org.rioproject.jsb.ServiceBeanAdapter;
import java.rmi.RemoteException;
import java.util.List;

/**
 * JSB for the Amazon S3 Storage Engine.
 * @author Jerome Bernard
 */
public class CloudFilesStorageEngineJSB extends ServiceBeanAdapter implements StorageEngine {
    private StorageEngine engine;

    @Override
    public void initialize(ServiceBeanContext context) throws Exception {
        super.initialize(context);
        String login = RackspaceUtils.getUsername();
        String apiKey = RackspaceUtils.getApiKey();
        engine = new CloudFilesStorageEngine(login, apiKey);
    }

    public String getStorageName() throws RemoteException {
        return engine.getStorageName();
    }

    public List<Container> getContainers() throws StorageException, RemoteException {
        return engine.getContainers();
    }

    public Container createContainer(String name) throws StorageException, RemoteException {
        return engine.createContainer(name);
    }

    public Container findContainerByName(String name) throws StorageException, RemoteException {
        return engine.findContainerByName(name);
    }

    public void deleteContainer(String name) throws StorageException, RemoteException {
        engine.deleteContainer(name);
    }
}