/**
 * Elastic Grid
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
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

package com.elasticgrid.tools.ui;

import com.elasticgrid.model.Cluster;
import javax.swing.*;
import java.util.List;
import java.util.Collections;

public class ClustersListModel extends AbstractListModel {
    private List<Cluster> clusters = Collections.emptyList();

    public int getSize() {
        return clusters.size();
    }

    public Object getElementAt(int index) {
        return clusters.get(index);
    }

    public void setClusters(List<Cluster> clusters) {
        this.clusters = clusters;
        super.fireContentsChanged(this, 0, clusters.size());
    }
}
