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
package com.elasticgrid.rackspace.cloudservers;

import java.io.Serializable;

/**
 * File description.
 * @author Jerome Bernard
 */
public class File implements Serializable {
    private final String path;
    private final byte[] base64Binary;

    public File(String path, byte[] base64Binary) {
        this.path = path;
        this.base64Binary = base64Binary;
    }

    public String getPath() {
        return path;
    }

    public byte[] getBase64Binary() {
        return base64Binary;
    }
}
