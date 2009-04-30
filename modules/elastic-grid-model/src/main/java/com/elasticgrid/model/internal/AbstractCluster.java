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

package com.elasticgrid.model.internal;

import com.elasticgrid.model.Application;
import com.elasticgrid.model.Cluster;
import com.elasticgrid.model.Node;
import com.elasticgrid.model.NodeProfile;
import java.net.InetAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Jerome Bernard
 */
public abstract class AbstractCluster<N extends Node> implements Cluster<N> {
    private String name;
    @SuppressWarnings("unchecked")
    private Set<N> nodes = setOfNodes();
    private Set<Application> applications = Factories.listOfApplications();

    protected abstract N createNode(NodeProfile profile);

    @SuppressWarnings("unchecked")
    private static Set setOfNodes() {
        return Collections.synchronizedSet(new HashSet());
    }

    public Cluster<N> name(String name) {
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

    public Set<N> getMonitorNodes() {
        Set<N> matches = new HashSet<N>();
        for (N node : nodes)
            if (NodeProfile.MONITOR.equals(node.getProfile()))
                matches.add(node);
        return matches;
    }

    public Set<N> getAgentNodes() {
        Set<N> matches = new HashSet<N>();
        for (N node : nodes)
            if (NodeProfile.AGENT.equals(node.getProfile()))
                matches.add(node);
        return matches;
    }

    @SuppressWarnings("unchecked")
    public N node(NodeProfile profile, InetAddress address) {
        N node = (N) createNode(profile).address(address);
        nodes.add(node);
        return node;
    }

    public Cluster<N> addNodes(Set<N> nodes) {
        this.nodes.addAll(nodes);
        return this;
    }

    public void setNodes(Set<N> nodes) {
        this.nodes = nodes;
    }

    public Set<Application> getApplications() {
        return applications;
    }

    public Application application(String name) {
        Application application = new ApplicationImpl().name(name);
        applications.add(application);
        return application;
    }

    public Cluster<N> addApplications(Set<Application> applications) {
        this.applications.addAll(applications);
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("AbstractCluster");
        sb.append("{name='").append(name).append('\'');
        sb.append(", nodes=").append(nodes.size());
        sb.append(", applications=").append(applications.size());
        sb.append('}');
        return sb.toString();
    }
}
