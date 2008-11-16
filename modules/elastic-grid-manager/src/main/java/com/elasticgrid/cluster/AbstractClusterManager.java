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

package com.elasticgrid.cluster;

import com.elasticgrid.model.Cluster;
import com.elasticgrid.model.ClusterException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.rmi.RemoteException;

/**
 * Abstract Cluster Manager which eases coding of {@link ClusterManager}s.
 * @author Jerome Bernard
 */
public abstract class AbstractClusterManager<C extends Cluster> implements ClusterManager<C> {

    public void startCluster(String clusterName) throws ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
        startCluster(clusterName, 1);
    }

    public void startCluster(String clusterName, int size) throws ClusterException, ExecutionException, TimeoutException, InterruptedException, RemoteException {
        // if no more than 2 nodes, only start monitors
        if (size <= 2)
            startCluster(clusterName, size, 0);
        // if more than 2 nodes, start 2 monitors and the other nodes as agents
        else
            startCluster(clusterName, 2, size - 2);
    }
    
}
