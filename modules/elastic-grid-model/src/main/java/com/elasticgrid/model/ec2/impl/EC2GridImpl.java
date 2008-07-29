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

import com.elasticgrid.model.NodeProfile;
import com.elasticgrid.model.ec2.EC2Grid;
import com.elasticgrid.model.ec2.EC2Node;
import com.elasticgrid.model.internal.AbstractGrid;
import java.net.InetAddress;

/**
 * @author Jerome Bernard
 */
public class EC2GridImpl extends AbstractGrid<EC2Node> implements EC2Grid {
    protected EC2Node createNode(NodeProfile profile) {
        return new EC2NodeImpl();
    }

    public EC2Node node(String instanceID, NodeProfile profile, InetAddress address) {
        return node(profile, address).instanceID(instanceID);
    }
}
