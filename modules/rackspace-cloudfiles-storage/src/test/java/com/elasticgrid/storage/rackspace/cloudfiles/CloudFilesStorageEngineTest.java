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
package com.elasticgrid.storage.rackspace.cloudfiles;

import com.elasticgrid.config.RackspaceConfiguration;
import com.elasticgrid.storage.StorageException;
import com.elasticgrid.storage.rackspace.CloudFilesStorageEngine;
import com.elasticgrid.storage.spi.AbstractStorageEngineTest;
import com.elasticgrid.storage.spi.StorageEngine;
import com.elasticgrid.utils.rackspace.RackspaceUtils;
import java.util.Properties;

/**
 * {@link CloudFilesStorageEngine} tests.
 *
 * @author Jerome Bernard
 */
public class CloudFilesStorageEngineTest extends AbstractStorageEngineTest {
    protected StorageEngine getStorageEngine() throws StorageException {
        try {
            Properties rackspaceConfig = RackspaceUtils.loadRackspaceConfiguration();
            String login = rackspaceConfig.getProperty(RackspaceConfiguration.LOGIN);
            String password = rackspaceConfig.getProperty(RackspaceConfiguration.API_KEY);
            if (login == null) {
                throw new IllegalArgumentException("Could not find Cloud Files login");
            }
            if (password == null) {
                throw new IllegalArgumentException("Could not find Cloud Files password");
            }
            return new CloudFilesStorageEngine(login, password);
        } catch (Exception e) {
            throw new StorageException("Can't initialize Cloud Files Storage Manager", e);
        }
    }
}