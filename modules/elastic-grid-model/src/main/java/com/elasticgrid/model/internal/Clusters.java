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

import com.elasticgrid.model.Cluster;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Clusters {
    private Set<Cluster> clusters = setOfClusters();

    public Clusters() {
    }

    public Clusters(List<Cluster> clusters) {
        this.clusters = new HashSet<Cluster>(clusters);
    }

    private static Set<Cluster> setOfClusters() {
        return Collections.synchronizedSet(new HashSet<Cluster>());
    }

    public Set<Cluster> getClusters() {
        return clusters;
    }

    public void addCluster(Cluster cluster) {
        clusters.add(cluster);
    }
}
