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

import com.elasticgrid.storage.Storable;
import com.mosso.client.cloudfiles.FilesClient;
import com.mosso.client.cloudfiles.FilesObject;
import com.mosso.client.cloudfiles.FilesAuthorizationException;
import com.mosso.client.cloudfiles.FilesInvalidNameException;
import java.io.InputStream;
import java.io.IOException;

/**
 * {@link Storable} providing support for Rackspace Cloud Files.
 *
 * @author Jerome Bernard
 */
public class CloudFilesStorable implements Storable {
    private final FilesClient rackspace;
    private final FilesObject object;

    public CloudFilesStorable(final FilesClient rackspace, final FilesObject object) {
        this.rackspace = rackspace;
        this.object = object;
    }

    public String getName() {
        return object.getName();
    }

    public InputStream getInputStream() throws IOException {
        try {
            return object.getObjectAsStream();
        } catch (Exception e) {
            throw new IOException("Can't get stream from storable", e);
        }
    }
}