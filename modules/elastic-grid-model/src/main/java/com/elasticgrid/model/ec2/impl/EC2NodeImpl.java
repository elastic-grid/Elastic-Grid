/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * Licensed under the GNU Lesser General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.elasticgrid.model.ec2.impl;

import com.elasticgrid.model.internal.AbstractNode;
import com.elasticgrid.model.ec2.EC2Node;
import com.elasticgrid.model.NodeProfile;
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
}
