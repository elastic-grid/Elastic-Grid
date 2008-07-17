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

import com.elasticgrid.model.Grid;
import com.elasticgrid.model.Node;
import com.elasticgrid.model.Application;
import com.elasticgrid.model.NodeProfile;

import java.util.*;
import java.net.InetAddress;

/**
 * @author Jerome Bernard
 */
public abstract class AbstractGrid<N extends Node> implements Grid<N> {
    private String name;
    @SuppressWarnings("unchecked")
    private Set<N> nodes = setOfNodes();
    private List<Application> applications = Factories.listOfApplications();

    protected abstract N createNode(NodeProfile profile);

    @SuppressWarnings("unchecked")
    private static Set setOfNodes() {
        return Collections.synchronizedSet(new HashSet());
    }

    public Grid name(String name) {
        setName(name);
        return this;
    }

    public boolean isRunning() {
        return nodes != null && nodes.size() > 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<N> getNodes() {
        return nodes;
    }

    @SuppressWarnings("unchecked")
    public N node(NodeProfile profile, InetAddress address) {
        N node = (N) createNode(profile).address(address);
        nodes.add(node);
        return node;
    }

    public void setNodes(Set<N> nodes) {
        this.nodes = nodes;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public Application application(String name) {
        Application application = new ApplicationImpl().name(name);
        applications.add(application);
        return application;
    }

    public Grid addNodes(List<N> nodes) {
        this.nodes.addAll(nodes);
        return this;
    }

}
