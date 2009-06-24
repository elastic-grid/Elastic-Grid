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
package com.elasticgrid.storage.rackspace.cloudfiles;

import com.elasticgrid.config.RackspaceConfiguration;
import com.elasticgrid.storage.AbstractStorageManagerTest;
import com.elasticgrid.storage.StorageException;
import com.elasticgrid.storage.StorageManager;
import com.elasticgrid.storage.rackspace.CloudFilesStorageManager;
import com.elasticgrid.utils.mosso.RackspaceUtils;

import java.util.Properties;

/**
 * {@link com.elasticgrid.storage.rackspace.CloudFilesStorageManager} tests.
 *
 * @author Jerome Bernard
 */
public class CloudFilesStorageManagerTest extends AbstractStorageManagerTest {
    protected StorageManager getStorageManager() throws StorageException {
        try {
            Properties rackspaceConfig = RackspaceUtils.loadMossoConfiguration();
            String login = rackspaceConfig.getProperty(RackspaceConfiguration.LOGIN);
            String password = rackspaceConfig.getProperty(RackspaceConfiguration.PASSWORD);
            if (login == null) {
                throw new IllegalArgumentException("Could not find Cloud Files login");
            }
            if (password == null) {
                throw new IllegalArgumentException("Could not find Cloud Files password");
            }
            return new CloudFilesStorageManager(login, password);
        } catch (Exception e) {
            throw new StorageException("Can't initialize Cloud Files Storage Manager", e);
        }
    }
}