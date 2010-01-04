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
package com.elasticgrid.storage.replicated;

import com.elasticgrid.storage.Container;
import com.elasticgrid.storage.Storable;
import com.elasticgrid.storage.StorageException;
import com.elasticgrid.storage.StorableNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.io.File;
import java.io.InputStream;

/**
 * Replicated {@link Container} ensuring that many {@link Container}s are kept in sync each time
 * a write operation is done. All read operations are done against a preferred {@link Container}
 *
 * @author Jerome Bernard
 */
public class ReplicatedContainer implements Container {
    private final Container preferred;
    private final List<Container> otherContainers;
    private static final Logger logger = Logger.getLogger(ReplicatedContainer.class.getName());

    public ReplicatedContainer(Container preferred, List<Container> otherContainers) {
        this.preferred = preferred;
        this.otherContainers = otherContainers;
    }

    public String getName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<Storable> listStorables() throws StorageException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Storable findStorableByName(String name) throws StorableNotFoundException, StorageException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Storable uploadStorable(File file) throws StorageException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Storable uploadStorable(String name, File file) throws StorageException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Storable uploadStorable(String name, InputStream stream, String mimeType) throws StorageException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void deleteStorable(String name) throws StorageException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
