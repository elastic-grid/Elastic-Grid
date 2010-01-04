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

import org.rioproject.associations.AssociationDescriptor;
import org.rioproject.associations.AssociationType;
import org.rioproject.associations.AssociationMgmt;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * {@link URLStreamHandler} for Elastic Grid Storage Layer.
 * The URL scheme for this handler is: <pre>storage://&lt;containername&gt;/&lt;storablename&gt;</pre>
 *
 * This handler has to be installed before any connection is made by using the following code:
 * <pre>URL.setURLStreamHandlerFactory(new StorageURLStreamHandlerFactory());</pre>
 * or must be set via a JVM property such as:
 * <pre>-Djava.protocol.handler.pkgs=com.elasticgrid</pre>
 *
 * @author Jerome Bernard
 */
public class Handler extends URLStreamHandler {
    private static StorageManager storageManager;
    private static final int TIMEOUT = 5 * 60 * 1000;   // 5 minutes
    private static final Logger logger = Logger.getLogger(Handler.class.getName());

    protected URLConnection openConnection(URL url) throws IOException {
        // build the association with the storage manager to fulfill
        AssociationDescriptor storageManagerAssociation = new AssociationDescriptor(AssociationType.REQUIRES);
        storageManagerAssociation.setMatchOnName(false);
        storageManagerAssociation.setInterfaceNames(StorageManager.class.getName());
        storageManagerAssociation.setGroups(System.getProperty("org.rioproject.groups"));

        // register the association listeners
        AssociationMgmt assocMgt = new AssociationMgmt();
        assocMgt.register(new StorageManagerListener());

        // search for the provision monitor
        assocMgt.addAssociationDescriptors(storageManagerAssociation);

        long before = System.currentTimeMillis();
        long now = System.currentTimeMillis();
        while ((now - before) < TIMEOUT) {
            logger.log(Level.WARNING, "Waiting for storage manager to be advertised...");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // ignore
            }
            now = System.currentTimeMillis();
        }
        return new StorageURLConnection(url, storageManager);
    }

    public static void setStorageManager(StorageManager storageManager) {
        Handler.storageManager = storageManager;
    }

}
