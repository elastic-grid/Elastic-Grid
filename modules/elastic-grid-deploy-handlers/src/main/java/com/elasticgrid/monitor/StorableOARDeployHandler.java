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

package com.elasticgrid.monitor;

import com.elasticgrid.storage.Container;
import com.elasticgrid.storage.Storable;
import com.elasticgrid.storage.StorageException;
import com.elasticgrid.storage.StorageManager;
import com.elasticgrid.storage.spi.StorageEngine;
import org.rioproject.core.OperationalString;
import org.rioproject.monitor.AbstractOARDeployHandler;
import org.rioproject.opstring.OAR;
import org.rioproject.opstring.OARUtil;
import org.rioproject.opstring.OpStringLoader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.rmi.RemoteException;

/**
 * A {@link org.rioproject.monitor.DeployHandler} that handles OAR files retrieved from any
 * {@link StorageManager} implementation.
 * <p/>
 * This <tt>DeployHandler</tt> uses two configuration settings;
 * <ul>
 *   <li>a <tt>dropContainer</tt>,</li>
 *   <li>an optional <tt>installDirectory</tt> which defaults to <tt>$EG_HOME/deploy</tt>.</li>
 * </ul>
 * <p>The <tt>dropContainer</tt> is polled for files having the <tt>oar</tt> extension.
 * If found, the OAR files are installed to the <tt>installDirectory</tt>, where
 * {@link org.rioproject.core.OperationalString}s are created and returned.
 *
 * @author Jerome Bernard
 */
public class StorableOARDeployHandler extends AbstractOARDeployHandler {
    private String dropContainer;
    private File installDirectory;
    private StorageManager storageManager;
    private boolean deleteAfterCopy;
    private final List<Storable> processedOARs = new ArrayList<Storable>();

//    static {
//        URL.setURLStreamHandlerFactory(new AWSURLStreamHandlerFactory());
//    }

    /**
     * Create a StorableOARDeployHandler with drop container and install directory
     *
     * @param dropContainer    The container where OAR files will be dropped
     * @param installDirectory The directory to install OARs into
     */
    public StorableOARDeployHandler(String dropContainer, File installDirectory) throws StorageException, RemoteException {
        this(dropContainer, installDirectory, true);
    }

    /**
     * Create a StorableOARDeployHandler with drop container and install directory
     *
     * @param dropContainer    The container where OAR files will be dropped
     * @param installDirectory The directory to install OARs into
     * @param deleteAfterCopy  Whether to remove the OAR from the container after copying it
     */
    public StorableOARDeployHandler(String dropContainer, File installDirectory, boolean deleteAfterCopy) throws StorageException, RemoteException {
        this.dropContainer = dropContainer;
        this.installDirectory = installDirectory;
        this.deleteAfterCopy = deleteAfterCopy;
//        try {
            storageManager = null;  // TODO; how to get it??
            StorageEngine storageEngine = storageManager.getPreferredStorageEngine();
            logger.log(Level.INFO, "Using {0}'s drop container {1}",
                    new Object[] { storageEngine.getStorageName(), dropContainer });
//        } catch (StorageException e) {
//            logger.log(Level.SEVERE, "Could not create StorableOARDeployHandler", e);
//        }

        // create install directory if needed
        if (!installDirectory.exists()) {
            if (installDirectory.mkdirs())
                logger.log(Level.INFO, "Created installDir {0}", installDirectory.getAbsolutePath());
        }

        // create the opstring loader
        try {
            opStringLoader = new OpStringLoader(this.getClass().getClassLoader());
        } catch (Exception e) {
            logger.log(Level.SEVERE,
                    "Could not create OpStringLoader, unable to deploy OperationalString Archive (OAR) files", e);
        }
    }

    protected List<OperationalString> look(Date from) {
        List<OperationalString> list = new ArrayList<OperationalString>();
        try {
            // get a handle to the container
            StorageEngine storageEngine = storageManager.getPreferredStorageEngine();
            Container container = storageEngine.findContainerByName(dropContainer);
            // retrieve the list of objects in the container
            List<Storable> storables = container.listStorables();
            // filter the list so that only OARs are kept
            for (ListIterator<Storable> iterator = storables.listIterator(); iterator.hasNext();) {
                Storable storable = iterator.next();
                if (!storable.getName().endsWith("oar"))
                    iterator.remove();
            }
            // process each OAR found
            for (Storable storable : storables) {
                try {
                    if(shouldDownload(storable)) {
                        doDownload(storable);
                    }
                } catch (IOException e) {
                    logger.log(Level.WARNING,
                               "Extracting [" + storable.getName() + "] to [" + installDirectory.getName() + "]", e);
                }
            }
            // parse the OAR file
            File[] files = installDirectory.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    try {
                        OAR oar = OARUtil.getOAR(file);
                        if (oar != null && oar.getActivationType().equals(OAR.AUTOMATIC)) {
                            list.addAll(parseOAR(oar, from));
                        }
                    } catch (IOException e) {
                        logger.log(Level.WARNING, "Loading [" + file.getName() + "]", e);
                    }
                }
            }
        } catch (StorageException e) {
            logger.log(Level.SEVERE, "Unexpected storable error", e);
        } catch (RemoteException e) {
            logger.log(Level.SEVERE, "Unexpected remote exception", e);
        }
        return list;
    }

    private boolean shouldDownload(Storable oar) {
        if (deleteAfterCopy)
            return true;
        boolean download = false;
        Storable processed = null;
        for (Storable processedOAR : processedOARs) {
            if (processedOAR.getName().equals(oar.getName())) {
                processed = processedOAR;
                break;
            }
        }
        /* If we have processed it see if it's updated. If it is download it. */
        if (processed != null) {
            Date processedDate = processed.getLastModifiedDate();
            Date oarDate = oar.getLastModifiedDate();
            if (oarDate.after(processedDate)) {
                download = true;
                synchronized (processedOARs) {
                    processedOARs.remove(processed);
                    processedOARs.add(oar);
                }
            }
        } else {
            download = true;
            processedOARs.add(oar);
        }
        return download;
    }

    private void doDownload(Storable oar) throws IOException {
        // TODO: write the "storage" protocol handler!!!
        install(new URL("storage://" + dropContainer + '/' + oar.getName()), installDirectory);
        if (deleteAfterCopy) {
            try {
                StorageEngine storageEngine = storageManager.getPreferredStorageEngine();
                Container container = storageEngine.findContainerByName(dropContainer);
                container.deleteStorable(oar.getName());
                if (logger.isLoggable(Level.FINE))
                    logger.fine("Deleted [" + oar.getName() + "] " +
                                "after installing it to " +
                                "[" + installDirectory.getAbsolutePath() + "]");
            } catch (StorageException e) {
                logger.log(Level.WARNING, "Could not delete {0} after installing it.", oar.getName());
            }
        }
    }
}