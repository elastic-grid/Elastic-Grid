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

package com.elasticgrid.model.lan.impl;

import com.elasticgrid.model.NodeProfile;
import com.elasticgrid.model.NodeType;
import com.elasticgrid.model.Node;
import com.elasticgrid.model.ec2.EC2Node;
import com.elasticgrid.model.lan.LANNode;
import com.elasticgrid.model.internal.AbstractNode;

/**
 * @author Jerome Bernard
 */
public class LANNodeImpl extends AbstractNode implements LANNode {
    private String instanceID;

    public LANNodeImpl() {
        super();
    }

    public LANNodeImpl(NodeProfile profile) {
        super(profile);
    }

    public String getID() {
        return getInstanceID();
    }

    public String getInstanceID() {
        return instanceID;
    }

    public void setInstanceID(String instanceID) {
        this.instanceID = instanceID;
    }

    public LANNode instanceID(String instanceID) {
        this.instanceID = instanceID;
        return this;
    }

    @Override
    public LANNode profile(NodeProfile profile) {
        return (LANNode) super.profile(profile);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("LANNodeImpl");
        sb.append("{instanceID='").append(instanceID).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public NodeType getType() {
        return new NodeType() {
            public String getName() {
                return "Unknown type";  // TODO: find a way to expose this!
            }
        };
    }
}