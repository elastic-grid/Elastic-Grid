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

package com.elasticgrid.model.rackspace.impl;

import com.elasticgrid.model.NodeProfile;
import com.elasticgrid.model.rackspace.CloudServersNode;
import com.elasticgrid.model.rackspace.CloudServersNodeType;
import com.elasticgrid.model.ec2.EC2Node;
import com.elasticgrid.model.ec2.EC2NodeType;
import com.elasticgrid.model.internal.AbstractNode;

/**
 * @author Jerome Bernard
 */
public class CloudServersNodeImpl extends AbstractNode implements CloudServersNode {
    private String serverID;
    private CloudServersNodeType type;

    public CloudServersNodeImpl() {
        super();
    }

    public CloudServersNodeImpl(NodeProfile profile, CloudServersNodeType type) {
        super(profile);
        setType(type);
    }

    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }

    public CloudServersNode serverID(String serverID) {
        this.serverID = serverID;
        return this;
    }

    public CloudServersNodeType getType() {
        return type;
    }

    public void setType(CloudServersNodeType type) {
        this.type = type;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("CloudServersNodeImpl");
        sb.append("{serverID='").append(serverID).append('\'');
        sb.append('}');
        return sb.toString();
    }
}