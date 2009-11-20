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
package com.elasticgrid.admin.client.widget.storage;

import com.elasticgrid.admin.client.AppEvents;
import com.elasticgrid.admin.model.StorageEngine;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import java.util.List;

public class StorageEnginesPanel extends LayoutContainer {
    private Grid<StorageEngine> grid;
    private ListStore<StorageEngine> store;

    public StorageEnginesPanel() {
//        ToolBar toolBar = new ToolBar();
//        Button create = new Button("Create");
//        create.setIcon(IconHelper.createStyle("icon-email-add"));
//        toolBar.add(create);
//        setTopComponent(toolBar);

        // column model
        List<ColumnConfig> columns = java.util.Arrays.asList(
                new ColumnConfig("name", "Name", 150),
                new ColumnConfig("number_of_containers", "Containers", 50)
        );
        ColumnModel cm = new ColumnModel(columns);

        store = new ListStore<StorageEngine>();

        grid = new Grid<StorageEngine>(store, cm);
        grid.getView().setForceFit(true);
        grid.setStyleAttribute("borderTop", "none");
        grid.setAutoExpandColumn("name");
        grid.setBorders(false);
//        grid.setStripeRows(true);
//        grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<Cluster>() {
//            @Override
//            public void selectionChanged(SelectionChangedEvent<Cluster> se) {
//                showCluster(se.getSelectedItem());
//            }
//        });

        add(grid);
    }

    public ListStore<StorageEngine> getStore() {
        return store;
    }

    public Grid<StorageEngine> getGrid() {
        return grid;
    }

    private void showStorageEngine(StorageEngine cluster) {
        AppEvent evt = new AppEvent(AppEvents.VIEW_STORAGE_ENGINE, cluster);
        Dispatcher.forwardEvent(evt);
    }

}