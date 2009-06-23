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

import com.elasticgrid.config.EC2Configuration;
import com.elasticgrid.config.MossoConfiguration;
import com.elasticgrid.storage.AbstractStorageManagerTest;
import com.elasticgrid.storage.StorageException;
import com.elasticgrid.storage.StorageManager;
import com.elasticgrid.utils.amazon.AWSUtils;
import com.elasticgrid.utils.mosso.MossoUtils;
import java.util.Properties;

/**
 * {@link MossoStorageManager} tests.
 *
 * @author Jerome Bernard
 */
public class MossoStorageManagerTest extends AbstractStorageManagerTest {
    protected StorageManager getStorageManager() throws StorageException {
        try {
            Properties mossoConfig = MossoUtils.loadMossoConfiguration();
            String login = mossoConfig.getProperty(MossoConfiguration.LOGIN);
            String password = mossoConfig.getProperty(MossoConfiguration.PASSWORD);
            if (login == null) {
                throw new IllegalArgumentException("Could not find Cloud Files login");
            }
            if (password == null) {
                throw new IllegalArgumentException("Could not find Cloud Files password");
            }
            return new MossoStorageManager(login, password);
        } catch (Exception e) {
            throw new StorageException("Can't initialize Cloud Files Storage Manager", e);
        }
    }
}