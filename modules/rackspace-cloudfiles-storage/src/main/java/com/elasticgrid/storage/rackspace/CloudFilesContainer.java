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
import com.elasticgrid.storage.StorageException;
import com.elasticgrid.storage.StorableNotFoundException;
import com.mosso.client.cloudfiles.FilesClient;
import com.mosso.client.cloudfiles.FilesContainer;
import com.mosso.client.cloudfiles.FilesObject;
import java.util.List;
import java.util.ArrayList;
import java.io.File;

import javax.activation.MimetypesFileTypeMap;

/**
 * {@link Container} providing support for Rackspace Cloud Files.
 *
 * @author Jerome Bernard
 */
public class CloudFilesContainer implements Container {
    private final FilesClient rackspace;
    private final FilesContainer rackspaceContainer;
    private final MimetypesFileTypeMap mimes;

    public CloudFilesContainer(final FilesClient rackspace, final FilesContainer rackspaceContainer) {
        this.rackspace = rackspace;
        this.rackspaceContainer = rackspaceContainer;
        this.mimes = new MimetypesFileTypeMap();
    }

    public String getName() {
        return rackspaceContainer.getName();
    }

    public List<Storable> listStorables() throws StorageException {
        try {
            rackspace.login();
            List<FilesObject> objects = rackspaceContainer.getObjects();
            List<Storable> storables = new ArrayList<Storable>(objects.size());
            for (FilesObject object : objects) {
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
            List<FilesObject> objects = rackspaceContainer.getObjects(name);
            if (objects.isEmpty())
                throw new StorableNotFoundException(name);
            else
                return new CloudFilesStorable(rackspace, objects.get(0)); 
        } catch (Exception e) {
            throw new StorageException("Can't find storage", e);
        }
    }

    public Storable uploadStorable(String key, File file) throws StorageException {
        try {
            rackspace.login();
            // upload the file
            rackspace.storeObjectAs(getName(), file, mimes.getContentType(file), key);
            // retrieve rackspace object
            List<FilesObject> objects = rackspaceContainer.getObjects(key);
            FilesObject object = null;
            for (FilesObject o : objects)
                if (o.getName().equals(file.getName()))
                    object = o;
            return new CloudFilesStorable(rackspace, object);
        } catch (Exception e) {
            throw new StorageException("Can't upload storable from file", e);
        }
    }
}
