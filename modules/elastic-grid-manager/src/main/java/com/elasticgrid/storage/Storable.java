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

import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.util.Date;

/**
 * A {@link Storable} is a file stored in a {@link Container} with associated metadata.
 * @author Jerome Bernard
 */
public interface Storable {

    /**
     * Return the name of the storable.
     * The name usually includes directory and subdirectory information and don't start with a forward slash.
     * @return the name of the storable
     */
    String getName();

    /**
     * Return the date of last modification.
     * This is mostly useful in order to check if a storable changed since last time it was downloaded/retreived.
     * @return the date of last modification
     */
    Date getLastModifiedDate();

    /**
     * Expose the storable content as an {@link InputStream}.
     * <strong>Note:</strong> it is the responsability of the caller to close the input stream!
     * @return the stream
     * @throws IOException
     * @see #asFile
     */
    InputStream asInputStream() throws IOException;

    /**
     * Expose the storable content as a {@link File}.
     * The {@link #asInputStream} alternative should be preferred most of the time in order to avoid file
     * creations and many superflous I/O operations.
     * @return the file
     * @throws java.io.IOException
     * @see #asInputStream
     */
    File asFile() throws IOException;
}
