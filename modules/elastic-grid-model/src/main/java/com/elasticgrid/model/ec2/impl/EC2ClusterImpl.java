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

package com.elasticgrid.model.ec2.impl;

import com.elasticgrid.model.NodeProfile;
import com.elasticgrid.model.ec2.EC2Cluster;
import com.elasticgrid.model.ec2.EC2Node;
import com.elasticgrid.model.internal.AbstractCluster;
import java.net.InetAddress;
import java.util.Set;

/**
 * @author Jerome Bernard
 */
public class EC2ClusterImpl extends AbstractCluster<EC2Node> implements EC2Cluster {
    protected EC2Node createNode(NodeProfile profile) {
        return new EC2NodeImpl(profile);
    }

    public EC2Node node(String instanceID, NodeProfile profile, InetAddress address) {
        return node(profile, address).instanceID(instanceID);
    }

    public boolean equals(Object o) {
        if (!(o instanceof EC2Cluster))
            return false;
        EC2Cluster anotherCluster = (EC2Cluster) o;
        if (!anotherCluster.getName().equals(getName()))
            return false;
        Set<EC2Node> otherNodes = anotherCluster.getNodes();
        return getNodes().equals(otherNodes);
    }
}
