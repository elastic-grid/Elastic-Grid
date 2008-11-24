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

package com.elasticgrid.model.internal;

import com.elasticgrid.model.Node;
import com.elasticgrid.model.NodeProfile;
import java.net.InetAddress;

/**
 * @author Jerome Bernard
 */
public abstract class AbstractNode implements Node {
    private NodeProfile profile;
    private InetAddress address;

    public AbstractNode() {}

    public AbstractNode(NodeProfile profile) {
        this.profile = profile;
    }

    public NodeProfile getProfile() {
        return profile;
    }

    public InetAddress getAddress() {
        return address;
    }

    public Node profile(NodeProfile profile) {
        this.profile = profile;
        return this;
    }

    public Node address(InetAddress address) {
        setAddress(address);
        return this;
    }

    public void setProfile(NodeProfile profile) {
        this.profile = profile;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("AbstractNode");
        sb.append("{profile=").append(profile);
        sb.append(", address=").append(address);
        sb.append('}');
        return sb.toString();
    }
}
