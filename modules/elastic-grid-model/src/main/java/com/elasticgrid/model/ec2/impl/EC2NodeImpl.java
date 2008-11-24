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

package com.elasticgrid.model.ec2.impl;

import com.elasticgrid.model.NodeProfile;
import com.elasticgrid.model.Node;
import com.elasticgrid.model.ec2.EC2Node;
import com.elasticgrid.model.internal.AbstractNode;
import java.net.InetAddress;

/**
 * @author Jerome Bernard
 */
public class EC2NodeImpl extends AbstractNode implements EC2Node {
    private String instanceID;

    public EC2NodeImpl() {
        super();
    }

    public EC2NodeImpl(NodeProfile profile) {
        super(profile);
    }

    public String getInstanceID() {
        return instanceID;
    }

    public void setInstanceID(String instanceID) {
        this.instanceID = instanceID;
    }

    public EC2Node instanceID(String instanceID) {
        this.instanceID = instanceID;
        return this;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("EC2NodeImpl");
        sb.append("{instanceID='").append(instanceID).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
