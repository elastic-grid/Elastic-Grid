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

package com.elasticgrid.cluster.discovery;

import com.elasticgrid.model.Node;
import com.elasticgrid.model.ClusterException;
import com.elasticgrid.model.ClusterNotFoundException;
import com.elasticgrid.model.ClusterMonitorNotFoundException;
import com.elasticgrid.model.Application;
import java.util.List;

/**
 * Cluster discovery.
 */
public interface ClusterLocator<N extends Node> {

    /**
     * Locate all clusters.
     * @return the clusters name.
     * @throws ClusterException if there is a technical error
     */
    List<String> findClusters() throws ClusterException;

    /**
     * Locate nodes which are part of a cluster.
     * @param clusterName the name of the cluster for whom nodes should be found
     * @return the list of {@link Node}s
     * @throws ClusterNotFoundException if the cluster can't be found
     * @throws ClusterException if there is a technical error
     */
    List<N> findNodes(String clusterName) throws ClusterException;

    /**
     * Locate a monitor instance in the specified cluster.
     * @param clusterName the name of the cluster for whom a monitor instance should be found
     * @return a monitor {@link Node}
     * @throws ClusterMonitorNotFoundException if the monitor's cluster can't be found
     */
    N findMonitor(String clusterName) throws ClusterMonitorNotFoundException;

    /**
     * Locate applications deployed on a cluster.
     * @param clusterName the name of the cluster for whom applications should be found
     * @return the list of {@link Application}s
     * @throws ClusterNotFoundException if the cluster can't be found
     * @throws ClusterException if there is a technical error
     */
    List<? extends Application> findApplications(String clusterName) throws ClusterException;

    /**
     * Add a {@link ClusterLocatorListener} to be notified of cluster topology changes.
     * @param listener the listener to be notified
     */
    void addClusterLocatorListener(ClusterLocatorListener listener);

    /**
     * Unsubscribe a {@link ClusterLocatorListener}.
     * @param listener the listener to unsubscribe
     */
    void removeClusterLocatorListener(ClusterLocatorListener listener);
}
