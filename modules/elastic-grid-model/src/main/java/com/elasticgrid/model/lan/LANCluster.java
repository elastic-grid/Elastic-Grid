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

package com.elasticgrid.model.lan;

import com.elasticgrid.model.Cluster;
import com.elasticgrid.model.Node;
import com.elasticgrid.model.NodeProfile;
import com.elasticgrid.model.ec2.EC2Node;
import java.net.InetAddress;

/**
 * Cluster built on top of a private LAN.
 * @author Jerome Bernard
 */
public interface LANCluster extends Cluster<LANNode> {
}