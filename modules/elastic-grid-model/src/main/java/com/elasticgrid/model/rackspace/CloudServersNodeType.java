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
package com.elasticgrid.model.rackspace;

import com.elasticgrid.model.NodeType;

/**
 * Enum of the type of Rackspace Cloud Servers instances.
 */
public enum CloudServersNodeType implements NodeType {
    MEM_256M_DISK_10GB("MEM_256M_DISK_10GB"),
	MEM_512M_DISK_20GB("MEM_512M_DISK_20GB"),
	MEM_1024M_DISK_40GB("MEM_1024M_DISK_40GB"),
	MEM_2048M_DISK_80GB("MEM_2048M_DISK_80GB"),
	MEM_4096M_DISK_160GB("MEM_4096M_DISK_160GB"),
	MEM_8192M_DISK_320GB("MEM_8192M_DISK_320GB"),
	MEM_15872M_DISK_620GB("MEM_15872M_DISK_620GB");

    private String name;

    CloudServersNodeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "CloudServersNodeType{" +
                "name='" + name + '\'' +
                '}';
    }
}