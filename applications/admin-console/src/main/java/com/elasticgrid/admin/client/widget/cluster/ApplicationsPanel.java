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

import com.elasticgrid.admin.model.Application;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import java.util.List;

public class ApplicationsPanel extends ContentPanel {
    private Grid<Application> grid;
    private ListStore<Application> store;
    private Button deploy;
    private Button undeploy;

    public ApplicationsPanel() {
        setHeading("Applications");
        setBorders(false);

        // column model
        List<ColumnConfig> columns = java.util.Arrays.asList(
                new ColumnConfig("name", "Name", 150),
                new ColumnConfig("number_of_services", "# of services", 50)
        );
        ColumnModel cm = new ColumnModel(columns);
        store = new ListStore<Application>();
        grid = new Grid<Application>(store, cm);
        grid.getView().setForceFit(true);
        grid.setStyleAttribute("borderTop", "none");
        grid.setAutoExpandColumn("name");
        grid.setBorders(false);
        grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<Application>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<Application> se) {
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

    public ListStore<Application> getStore() {
        return store;
    }

    public Grid<Application> getGrid() {
        return grid;
    }

//    public void showCluster(Cluster cluster) {
//    }

}