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

import com.elasticgrid.model.Cluster;
import com.elasticgrid.model.Node;
import com.elasticgrid.model.NodeProfile;
import java.net.InetAddress;

/**
 * Cluster built on top of Rackspace Cloud Servers.
 * @author Jerome Bernard
 */
public interface CloudServersCluster extends Cluster<CloudServersNode> {
    Node node(String instanceID, NodeProfile profile, CloudServersNodeType type, InetAddress address);
}