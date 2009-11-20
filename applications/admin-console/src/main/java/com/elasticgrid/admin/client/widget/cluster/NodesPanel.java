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

import com.elasticgrid.admin.model.Node;
import com.elasticgrid.admin.client.AppEvents;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.util.IconHelper;
import java.util.List;

public class NodesPanel extends ContentPanel {
    private Grid<Node> grid;
    private ListStore<Node> store;
    private Button resize;
    private Button stop;

    public NodesPanel() {
        setLayout(new FitLayout());
        setHeading("Nodes");
        setIcon(IconHelper.createStyle("icon-nodes"));
        setBorders(false);

        // column model
        ColumnConfig idColumn = new ColumnConfig("id", "ID", 0);
        idColumn.setHidden(true);
        List<ColumnConfig> columns = java.util.Arrays.asList(
                idColumn,
                new ColumnConfig("name", "Name", 150),
                new ColumnConfig("profile", "Profile", 150),
                new ColumnConfig("type", "Type", 150)
        );
        ColumnModel cm = new ColumnModel(columns);
        store = new ListStore<Node>();
        grid = new Grid<Node>(store, cm);
        grid.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
//        grid.getView().setForceFit(true);
        grid.setStyleAttribute("borderTop", "none");
        grid.setAutoExpandColumn("name");
        grid.setBorders(false);
        grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<Node>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<Node> se) {
                if (se.getSelectedItem() != null)
                    stop.setEnabled(true);
                else
                    stop.setEnabled(false);
            }
        });
        add(grid);

        ToolBar toolBar = new ToolBar();
        toolBar.setAlignment(HorizontalAlignment.LEFT);
        resize = new Button("Resize cluster");
        resize.setIcon(IconHelper.createStyle("icon-cluster-resize"));
        resize.setToolTip(new ToolTipConfig(
                "Resize cluster",
                "Change cluster topology by adding/stopping servers."
        ));
        resize.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                AddNodeDialog dialog = new AddNodeDialog();
                dialog.setClosable(false);
//                dialog.addListener(Events.Hide, new Listener<WindowEvent>() {
//                    public void handleEvent(WindowEvent be) {
//                        Dispatcher.forwardEvent(AppEvents.INIT);
//                    }
//                });
                dialog.show();
            }
        });
        toolBar.add(resize);
        stop = new Button("Stop node");
        stop.setEnabled(false);
        stop.setIcon(IconHelper.createStyle("icon-node-delete"));
        stop.setToolTip(new ToolTipConfig(
                "Stop node",
                "Stop the currently selected node. " +
                        "Deployed services will eventually be relocated depending on the SLA requirements."
        ));
        stop.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                Node node = grid.getSelectionModel().getSelectedItem();
                AppEvent ae = new AppEvent(AppEvents.STOP_NODE);
                ae.setData("node", node);
                Dispatcher.forwardEvent(ae);
            }
        });
        toolBar.add(stop);
        setTopComponent(toolBar);

        layout();
    }

    public ListStore<Node> getStore() {
        return store;
    }

    public Grid<Node> getGrid() {
        return grid;
    }
}