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
package com.elasticgrid.model.lan.impl;

import com.elasticgrid.model.NodeProfile;
import com.elasticgrid.model.NodeType;
import com.elasticgrid.model.internal.AbstractCluster;
import com.elasticgrid.model.lan.LANCluster;
import com.elasticgrid.model.lan.LANNode;
import java.util.Set;

/**
 * @author Jerome Bernard
 */
public class LANClusterImpl extends AbstractCluster<LANNode, NodeType> implements LANCluster {
    protected LANNode createNode(NodeProfile profile, NodeType type) {
        return new LANNodeImpl(profile);
    }

    public boolean equals(Object o) {
        if (!(o instanceof LANCluster))
            return false;
        LANCluster anotherCluster = (LANCluster) o;
        if (!anotherCluster.getName().equals(getName()))
            return false;
        Set<LANNode> otherNodes = anotherCluster.getNodes();
        return getNodes().equals(otherNodes);
    }
}
