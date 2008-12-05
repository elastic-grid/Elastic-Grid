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
import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.Collections;

public class ClustersTableModel extends AbstractTableModel {
    private List<Cluster> clusters = Collections.emptyList();

    public int getRowCount() {
        return clusters.size();
    }

    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Cluster Name";
            case 1:
                return "Number of nodes";
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return Integer.class;
            default:
                throw new IllegalStateException();
        }
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Cluster cluster = clusters.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return cluster.getName();
            case 1:
                return cluster.getNodes().size();
            default:
                throw new IllegalStateException();
        }
    }

    public void setClusters(List<Cluster> clusters) {
        this.clusters = clusters;
        super.fireTableDataChanged();
    }
}
