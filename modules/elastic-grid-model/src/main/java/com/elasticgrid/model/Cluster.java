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
package com.elasticgrid.model;

import java.io.Serializable;
import java.util.Set;

/**
 * A cluster is a set of {@link Node}s that are linked together
 * and can be used by one or more monitor in order to deploy
 * applications.
 * @author Jerome Bernard
 */
public interface Cluster<N extends Node> extends Serializable {
    /**
     * Get the name of the cluster.
     * @return the name of the cluster
     */
    String getName();

    /**
     * Set the name of the cluster.
     * @param name the name of the cluster
     * @return the cluster
     */
    Cluster<N> name(String name);

    /**
     * Give the running status of the cluster.
     * TODO: this method should be enhanced so that we can running different statuses like "pending" or so.
     * @return true if the cluster is running
     */
    boolean isRunning();

    /**
     * Return the set of {@link Node}s which are members of this cluster.
     * @return the nodes
     */
    Set<N> getNodes();

    /**
     * Return the set of {@link Node}s which are members of this cluster and
     * are of {@link NodeProfile#MONITOR} or {@link NodeProfile#MONITOR_AND_AGENT}.
     * @return the monitors nodes
     */
    Set<N> getMonitorNodes();

    /**
     * Return the set of {@link Node}s which are members of this cluster and
     * are of {@link NodeProfile#AGENT} or {@link NodeProfile#MONITOR_AND_AGENT}.
     * @return the agents nodes
     */
    Set<N> getAgentNodes();

    /**
     * Add nodes into the cluster.
     * Note: this method does NOT start nodes, it only modifies the list of nodes in the cluster;
     * it should not be called most of the time.
     * @param nodes the list of nodes to add to the cluster
     * @return the cluster
     */
    Cluster<N> addNodes(Set<N> nodes);

    /**
     * Return the set of applications deployed within the cluster.
     * @return the set of applications
     */
    Set<Application> getApplications();

    /**
     * Add application into the cluster.
     * Note: this method does NOT deploy applications, it only modifies the list of applications in the cluster;
     * it should not be called most of the time.
     * @param name the name of the new application
     * @return the application created
     */
    Application application(String name);

    /**
     * Add applications into the cluster.
     * Note: this method does NOT deploy applications, it only modifies the list of applications in the cluster;
     * it should not be called most of the time.
     * @param applications the applications
     * @return the cluster
     */
    Cluster<N> addApplications(Set<Application> applications);

    /**
     * Status of the cluster.
     */
    enum Status {
        RUNNING, STOPPED
    }
}
