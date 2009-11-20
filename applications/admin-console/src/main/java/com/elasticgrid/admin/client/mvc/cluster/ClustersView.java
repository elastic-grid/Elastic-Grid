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
package com.elasticgrid.admin.client.mvc.cluster;

import com.elasticgrid.admin.client.AppEvents;
import com.elasticgrid.admin.client.mvc.AppView;
import com.elasticgrid.admin.client.widget.cluster.ClustersPanel;
import com.elasticgrid.admin.model.Cluster;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import java.util.List;

public class ClustersView extends View {
    private ClustersPanel clustersPanel;
    public static final String CENTER = "tab_storage_center";

    public ClustersView(Controller controller) {
        super(controller);
    }

    protected void initUI() {
        TabItem container = (TabItem) Registry.get(AppView.TAB_CLUSTER);
        container.setLayout(new BorderLayout());

        clustersPanel = new ClustersPanel();
        clustersPanel.setAnimCollapse(false);
        clustersPanel.setHeading("Clusters");
        clustersPanel.getGrid().getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<Cluster>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<Cluster> se) {
                AppEvent evt = new AppEvent(AppEvents.VIEW_CLUSTER, se.getSelectedItem());
                Dispatcher.forwardEvent(evt);
            }
        });
        container.add(clustersPanel, new BorderLayoutData(LayoutRegion.WEST, 200));

        LayoutContainer center = new LayoutContainer();
        center.setBorders(false);
        container.add(center, new BorderLayoutData(LayoutRegion.CENTER));

        Registry.register(CENTER, center);

        container.layout();
    }

    @Override
    protected void handleEvent(AppEvent event) {
        if (event.getType() == AppEvents.INIT) {
            initUI();
        } else if (event.getType() == AppEvents.NAV_CLUSTERS) {
            List<Cluster> clusters = event.getData();
            if (clusters != null) {
                clustersPanel.getStore().add(clusters);
            }
        }
    }
}
