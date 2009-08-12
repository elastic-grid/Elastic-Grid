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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * {@link URLConnection} for Elastic Grid Storage Layer.
 *
 * @author Jerome Bernard
 */
public class StorageURLConnection extends URLConnection {
    private StorageManager storage;
    private static final Logger logger = Logger.getLogger(StorageURLConnection.class.getName());

    public StorageURLConnection(URL url, StorageManager storage) {
        super(url);
        this.storage = storage;
    }

    public void connect() throws IOException {
    }

    @Override
    public InputStream getInputStream() throws IOException {
        String engineName = null;
        String containerName = url.getHost();
        String storableName = url.getPath().substring(1);
        try {
            StorageEngine engine = storage.getPreferredStorageEngine();
            Container container = engine.findContainerByName(containerName);
            Storable storable = container.findStorableByName(storableName);
            return storable.asInputStream();
        } catch (StorageException e) {
            logger.log(Level.SEVERE,
                    "Could not get storable ''{1}'' from container ''{2}'' using engine {3}",
                    new Object[] { storableName, containerName, engineName });
            throw new IOException(e.toString());
        }
    }
}