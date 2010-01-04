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
package com.elasticgrid.cluster.discovery;

import com.elasticgrid.model.Cluster;
import com.elasticgrid.model.Node;
import java.util.EventObject;

/**
 * Event generated when a grid topology changed.
 */
public class ClusterChangedEvent<N extends Node> extends EventObject {
    private Cluster<N> cluster;

    /**
     * Constructs a {@link ClusterChangedEvent} Event.
     *
     * @param source The object on which the Event initially occurred.
     * @param cluster the cluster which changed
     * @throws IllegalArgumentException if source is null.
     */
    public ClusterChangedEvent(Object source, Cluster<N> cluster) {
        super(source);
        this.cluster = cluster;
    }

    public Cluster<N> getCluster() {
        return cluster;
    }
}
