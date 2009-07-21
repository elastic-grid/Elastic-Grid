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

package com.elasticgrid.config;

/**
 * Generic configuration properties available across all Cloud platforms.
 *
 * @author Jerome Bernard
 */
public interface GenericConfiguration {
    public static final String EG_CLUSTER_NAME  = "eg.cluster.name";
    public static final String EG_DROP_BUCKET = "eg.s3.dropBucket";
    public static final String EG_OVERRIDES_BUCKET = "eg.s3.overridesBucket";
    /* Either EC2 or LAN, default to EC2 if not declared */
    public static final String EG_MODE = "eg.mode";
}
