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

import com.elasticgrid.admin.model.Service;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.allen_sauer.gwt.log.client.Log;
import java.util.List;

public class ServicesPanel extends ContentPanel {
    private Grid<Service> grid;
    private GroupingStore<Service> store;
    private Button deploy;
    private Button undeploy;

    public ServicesPanel() {
        setLayout(new FitLayout());
        setHeading("Applications");
        setIcon(IconHelper.createStyle("icon-applications"));
        setBorders(false);

        // column model
        List<ColumnConfig> columns = java.util.Arrays.asList(
                new ColumnConfig("name", "Name", 150),
                new ColumnConfig("application", "Application", 150)
        );
        final ColumnModel cm = new ColumnModel(columns);
        store = new GroupingStore<Service>();
        store.groupBy("application");

        GroupingView view = new GroupingView();
        view.setShowGroupedColumn(false);
        view.setForceFit(true);
        view.setGroupRenderer(new GridGroupRenderer() {
            public String render(GroupColumnData data) {
                String f = cm.getColumnById(data.field).getHeader();
                String l = data.models.size() == 1 ? "Service" : "Services";
                return f + ": " + data.group + " (" + data.models.size() + " " + l + ")";
            }
        });

        grid = new Grid<Service>(store, cm);
        grid.setView(view);
        grid.setStyleAttribute("borderTop", "none");
        grid.setAutoExpandColumn("name");
        grid.setBorders(false);
        grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<Service>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<Service> se) {
                if (se.getSelectedItem() != null)
                    undeploy.setEnabled(true);
                else
                    undeploy.setEnabled(false);
            }
        });
        add(grid);

        ToolBar toolBar = new ToolBar();
        toolBar.setAlignment(HorizontalAlignment.LEFT);
        deploy = new Button("Deploy");
        deploy.setIcon(IconHelper.createStyle("icon-application-add"));
        deploy.setToolTip(new ToolTipConfig(
                "Deploy new application",
                "Deploy a new application in the cluster."
        ));
        toolBar.add(deploy);
        undeploy = new Button("Undeploy");
        undeploy.setIcon(IconHelper.createStyle("icon-application-delete"));
        undeploy.setToolTip(new ToolTipConfig(
                "Undeploy selection application",
                "Immediately undeploy the selected application."
        ));
        undeploy.setEnabled(false);
        toolBar.add(undeploy);
        setTopComponent(toolBar);

        layout();
    }

    public GroupingStore<Service> getStore() {
        return store;
    }

    public Grid<Service> getGrid() {
        return grid;
    }

//    public void showCluster(Cluster cluster) {
//    }

}