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
package com.elasticgrid.storage.rackspace;

import com.elasticgrid.storage.Container;
import com.elasticgrid.storage.Storable;
import com.elasticgrid.storage.StorableNotFoundException;
import com.elasticgrid.storage.StorageException;
import com.mosso.client.cloudfiles.FilesClient;
import com.mosso.client.cloudfiles.FilesContainer;
import com.mosso.client.cloudfiles.FilesObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.reflect.Field;

/**
 * {@link Container} providing support for Rackspace Cloud Files.
 *
 * @author Jerome Bernard
 */
public class CloudFilesContainer implements Container {
    private final FilesClient rackspace;
    private final FilesContainer rackspaceContainer;
    private final MimetypesFileTypeMap mimes;
    private Field mimeTypeField;
    private final Logger logger = Logger.getLogger(CloudFilesStorageManager.class.getName());

    public CloudFilesContainer(final FilesClient rackspace, final FilesContainer rackspaceContainer) throws NoSuchFieldException {
        this.rackspace = rackspace;
        this.rackspaceContainer = rackspaceContainer;
        this.mimes = new MimetypesFileTypeMap();
        mimeTypeField = FilesObject.class.getDeclaredField("mimeType");
        mimeTypeField.setAccessible(true);
    }

    public String getName() {
        return rackspaceContainer.getName();
    }

    public List<Storable> listStorables() throws StorageException {
        try {
            rackspace.login();
            List<FilesObject> objects = rackspace.listObjects(getName());
            List<Storable> storables = new ArrayList<Storable>(objects.size());
            for (FilesObject object : objects) {
                // small hack in order to fetch the mime type of the underlying object                
                String mimeType = (String) mimeTypeField.get(object);
                if (!"application/directory".equals(mimeType))      // skip directories
                    storables.add(new CloudFilesStorable(rackspace, object));
            }
            return storables;
        } catch (Exception e) {
            throw new StorageException("Can't list storables", e);
        }
    }

    public Storable findStorableByName(String name) throws StorableNotFoundException, StorageException {
        try {
            rackspace.login();
            int hasPath = name.lastIndexOf('/');
            List<FilesObject> objects = null;
            if (hasPath == -1) {
                objects = rackspace.listObjects(getName());
            } else {
                String path = name.substring(0, hasPath);
                objects = rackspace.listObjects(getName(), path);
            }
            if (objects.isEmpty())
                throw new StorableNotFoundException(name);
            FilesObject found = null;
            for (FilesObject object : objects) {
                if (object.getName().equals(name)) {
                    found = object;
                    break;
                }
            }
            if (found == null)
                throw new StorableNotFoundException(name);
            else
                return new CloudFilesStorable(rackspace, found);
        } catch (StorableNotFoundException snfe) {
            throw snfe;
        } catch (Exception e) {
            throw new StorageException("Can't find storage", e);
        }
    }

    public Storable uploadStorable(File file) throws StorageException {
        return uploadStorable(file.getName(), file);
    }

    public Storable uploadStorable(String name, File file) throws StorageException {
        try {
            rackspace.login();
            // create directories if needed
            String path = name.substring(0, name.lastIndexOf('/'));
            logger.log(Level.FINE, "Creating full path \"{0}\"", path);
            rackspace.createFullPath(getName(), path);
            // upload the file
            logger.log(Level.FINE, "Uploading \"{0}\"", name);
            InputStream stream = null;
            try {
                stream = FileUtils.openInputStream(file);
                rackspace.storeStreamedObject(getName(), stream, mimes.getContentType(file), name,
                        Collections.<String, String>emptyMap());
            } finally {
                IOUtils.closeQuietly(stream);
            }
            // retrieve rackspace object
            return findStorableByName(name);
        } catch (Exception e) {
            throw new StorageException("Can't upload storable from file", e);
        }
    }

    public Storable uploadStorable(String name, InputStream stream, String mimeType) throws StorageException {
        try {
            rackspace.login();
            // create directories if needed
            String path = name.substring(0, name.lastIndexOf('/'));
            logger.log(Level.FINE, "Creating full path \"{0}\"", path);
            rackspace.createFullPath(getName(), path);
            // upload the file
            logger.log(Level.FINE, "Uploading \"{0}\"", name);
            try {
                rackspace.storeStreamedObject(getName(), stream, mimeType, name,
                        Collections.<String, String>emptyMap());
            } finally {
                IOUtils.closeQuietly(stream);
            }
            // retrieve rackspace object
            return findStorableByName(name);
        } catch (Exception e) {
            throw new StorageException("Can't upload storable from stream", e);
        }
    }
}
