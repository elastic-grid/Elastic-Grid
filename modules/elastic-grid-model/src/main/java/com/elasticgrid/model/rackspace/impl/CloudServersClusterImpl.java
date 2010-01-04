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
package com.elasticgrid.model.rackspace.impl;

import com.elasticgrid.model.NodeProfile;
import com.elasticgrid.model.rackspace.CloudServersNodeType;
import com.elasticgrid.model.rackspace.CloudServersNode;
import com.elasticgrid.model.rackspace.CloudServersCluster;
import com.elasticgrid.model.ec2.EC2Cluster;
import com.elasticgrid.model.ec2.EC2Node;
import com.elasticgrid.model.ec2.EC2NodeType;
import com.elasticgrid.model.ec2.impl.EC2NodeImpl;
import com.elasticgrid.model.internal.AbstractCluster;
import java.net.InetAddress;
import java.util.Set;

/**
 * @author Jerome Bernard
 */
public class CloudServersClusterImpl extends AbstractCluster<CloudServersNode, CloudServersNodeType> implements CloudServersCluster {
    protected CloudServersNode createNode(NodeProfile profile, CloudServersNodeType type) {
        return new CloudServersNodeImpl(profile, type);
    }

    public CloudServersNode node(String serverID, NodeProfile profile, CloudServersNodeType type, InetAddress address) {
        return node(profile, type, address).serverID(serverID);
    }

    public boolean equals(Object o) {
        if (!(o instanceof CloudServersCluster))
            return false;
        CloudServersCluster anotherCluster = (CloudServersCluster) o;
        if (!anotherCluster.getName().equals(getName()))
            return false;
        Set<CloudServersNode> otherNodes = anotherCluster.getNodes();
        return getNodes().equals(otherNodes);
    }
}