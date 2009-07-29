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
 * Hardware configuration for a server.
 * @author Jerome Bernard 
 */
public class Flavor implements Serializable {
    private final int id;
    private final String name;
    private final Integer ram;
    private final Integer disk;

    public Flavor(int id, String name, Integer ram, Integer disk) {
        this.id = id;
        this.name = name;
        this.ram = ram;
        this.disk = disk;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getRam() {
        return ram;
    }

    public Integer getDisk() {
        return disk;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Flavor");
        sb.append("{id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", ram=").append(ram);
        sb.append(", disk=").append(disk);
        sb.append('}');
        return sb.toString();
    }
}
