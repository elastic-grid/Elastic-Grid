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

package com.elasticgrid.tools.ui;

import com.elasticgrid.model.Grid;
import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.Collections;

public class GridsTableModel extends AbstractTableModel {
    private List<Grid> grids = Collections.emptyList();

    public int getRowCount() {
        return grids.size();
    }

    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Grid Name";
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
        Grid grid = grids.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return grid.getName();
            case 1:
                return grid.getNodes().size();
            default:
                throw new IllegalStateException();
        }
    }

    public void setGrids(List<Grid> grids) {
        this.grids = grids;
        super.fireTableDataChanged();
    }
}
