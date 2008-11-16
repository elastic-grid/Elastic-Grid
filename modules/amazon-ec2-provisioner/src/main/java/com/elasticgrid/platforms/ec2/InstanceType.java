/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This file is part of Elastic Grid.
 * 
 * Elastic Grid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * 
 * Elastic Grid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Elastic Grid.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elasticgrid.platforms.ec2;

/**
 * Enum of the type of Amazon EC2 instances.
 */
public enum InstanceType {
    SMALL("m1.small"),
	LARGE("m1.large"),
	EXTRA_LARGE("m1.xlarge"),
	MEDIUM_HIGH_CPU("c1.medium"),
	EXTRA_LARGE_HIGH_CPU("c1.xlarge");

    private String name;

    InstanceType(String name) {
        this.name = name;
    }

    public Object getName() {
        return name;
    }

    public String toString() {
        return "InstanceType{" +
                "name='" + name + '\'' +
                '}';
    }
}
