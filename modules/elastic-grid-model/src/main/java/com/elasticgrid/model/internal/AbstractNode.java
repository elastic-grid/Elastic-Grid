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

    public void setAddress(InetAddress address) {
        this.address = address;
    }
}
