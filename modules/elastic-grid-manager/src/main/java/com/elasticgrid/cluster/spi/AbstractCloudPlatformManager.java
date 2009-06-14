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
package com.elasticgrid.cluster.spi;

import com.elasticgrid.model.Cluster;
import com.elasticgrid.model.ClusterException;
import java.rmi.RemoteException;
import java.util.Collection;

/**
 * Abstract class helping {@link CloudPlatformManager} implementations.
 * @author Jerome Bernard
 */
public abstract class AbstractCloudPlatformManager<C extends Cluster> implements CloudPlatformManager<C> {
    public Statistics getStatistics() throws RemoteException {
        return new Statistics() {
            public int getNumberOfClusters() throws ClusterException, RemoteException {
                return findClusters().size();
            }

            public int getNumberOfNodes() throws ClusterException, RemoteException {
                Collection<C> clusters = findClusters();
                int count = 0;
                for (C cluster : clusters)
                    count += cluster.getNodes().size();
                return count;
            }
        };
    }
}
