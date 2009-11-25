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
package com.elasticgrid.admin.client.widget.cluster;

import com.elasticgrid.admin.client.AppEvents;
import com.elasticgrid.admin.model.Cluster;
import com.elasticgrid.admin.model.Node;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import java.util.List;

public class ClustersPanel extends ContentPanel {
    private Grid<Cluster> grid;
    private ListStore<Cluster> store;
    private Button start;
    private Button stop;

    public ClustersPanel() {
        setLayout(new FitLayout());

        // column model
        List<ColumnConfig> columns = java.util.Arrays.asList(
                new ColumnConfig("name", "Name", 150),
                new ColumnConfig("number_of_nodes", "Nodes", 50)
        );
        ColumnModel cm = new ColumnModel(columns);

        store = new ListStore<Cluster>();

        grid = new Grid<Cluster>(store, cm);
        grid.getView().setForceFit(true);
        grid.setStyleAttribute("borderTop", "none");
        grid.setAutoExpandColumn("name");
        grid.setBorders(false);
//        grid.setStripeRows(true);
        grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<Cluster>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<Cluster> se) {
                if (se.getSelectedItem() != null)
                    stop.setEnabled(true);
                else
                    stop.setEnabled(false);
            }
        });

        ToolBar toolBar = new ToolBar();
        toolBar.setAlignment(HorizontalAlignment.LEFT);
        start = new Button("Start cluster");
        start.setIcon(IconHelper.createStyle("icon-cluster-start"));
        start.setToolTip(new ToolTipConfig(
                "Start a new cluster",
                "Start a new cluster in which new applications can be deployed. " +
                        "You have control over the number of servers and type of servers to start."
        ));
        start.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                StartClusterDialog dialog = new StartClusterDialog();
                dialog.setClosable(false);
                dialog.show();                
            }
        });
        toolBar.add(start);
        stop = new Button("Stop cluster");
        stop.setIcon(IconHelper.createStyle("icon-cluster-delete"));
        stop.setToolTip(new ToolTipConfig(
                "Stop selected cluster",
                "Immediately stop the selected cluster and the applications running in this cluster."
        ));
        stop.setEnabled(false);
        toolBar.add(stop);

        setTopComponent(toolBar);

        add(grid);

        layout();
    }

    public ListStore<Cluster> getStore() {
        return store;
    }

    public Grid<Cluster> getGrid() {
        return grid;
    }

}
