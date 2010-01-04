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
package com.elasticgrid.admin.client.widget.storage;

import com.elasticgrid.admin.model.Container;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import java.util.List;

public class ContainersPanel extends ContentPanel {
    private TreeGrid<Container> grid;
    private TreeStore<Container> store;

    public ContainersPanel() {
        // column model
        ColumnConfig name = new ColumnConfig("name", "Name", 250);
        name.setRenderer(new TreeGridCellRenderer());
        List<ColumnConfig> columns = java.util.Arrays.asList(
                name,
                new ColumnConfig("last_modification_date", "Last Modification", 200)
        );
        ColumnModel cm = new ColumnModel(columns);
        store = new TreeStore<Container>();
        grid = new TreeGrid<Container>(store, cm);
        grid.setBorders(true);
        grid.getView().setForceFit(true);
        grid.setAutoExpandColumn("name");
        grid.setTrackMouseOver(false);  
        add(grid);
        layout();
    }

    public TreeStore<Container> getStore() {
        return store;
    }

    public TreeGrid<Container> getGrid() {
        return grid;
    }

}