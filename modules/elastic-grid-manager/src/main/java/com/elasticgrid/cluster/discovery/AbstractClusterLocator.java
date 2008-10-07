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

package com.elasticgrid.cluster.discovery;

import com.elasticgrid.model.Node;
import javax.swing.event.EventListenerList;

/**
 * Abstract {@link ClusterLocator} simplifying implementation by subclasses.
 */
public abstract class AbstractClusterLocator<N extends Node> implements ClusterLocator<N> {
    protected EventListenerList listeners = new EventListenerList();

    protected void fireClusterChangedEvent(ClusterChangedEvent event) {
        Object[] l = listeners.getListenerList();
        for (int i = 0; i < l.length; i += 2) {
            if (l[i] == ClusterLocatorListener.class) {
                ((ClusterLocatorListener) l[i + 1]).clusterChanged(event);
            }
        }
    }

    public void addClusterLocatorListener(ClusterLocatorListener listener) {
        listeners.add(ClusterLocatorListener.class, listener);
    }

    public void removeClusterLocatorListener(ClusterLocatorListener listener) {
        listeners.remove(ClusterLocatorListener.class, listener);
    }
}
