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

import com.elasticgrid.model.Node;
import com.elasticgrid.model.NodeProfile;
import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.Collections;
import java.util.Set;
import java.util.ArrayList;
import java.net.InetAddress;

public class NodesTableModel extends AbstractTableModel {
    private List<Node> nodes = Collections.emptyList();

    public int getRowCount() {
        return nodes.size();
    }

    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Profile";
            case 1:
                return "Address";
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return NodeProfile.class;
            case 1:
                return InetAddress.class;
            default:
                throw new IllegalStateException();
        }
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Node node = nodes.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return node.getProfile();
            case 1:
                return node.getAddress();
            default:
                throw new IllegalStateException();
        }
    }

    public void setNodes(Set<Node> nodes) {
        this.nodes = new ArrayList<Node>(nodes);
        super.fireTableDataChanged();
    }
}