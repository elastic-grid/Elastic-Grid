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
