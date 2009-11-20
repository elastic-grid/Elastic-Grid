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

import com.allen_sauer.gwt.log.client.Log;
import com.elasticgrid.admin.client.AppEvents;
import com.elasticgrid.admin.client.widget.cluster.NodesPanel;
import com.elasticgrid.admin.client.widget.cluster.ServicesPanel;
import com.elasticgrid.admin.client.widget.cluster.WatchesPanel;
import com.elasticgrid.admin.client.widget.cluster.WatchesPanel.Type;
import com.elasticgrid.admin.model.Application;
import com.elasticgrid.admin.model.Cluster;
import com.elasticgrid.admin.model.Node;
import com.elasticgrid.admin.model.Service;
import com.elasticgrid.admin.model.Watch;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClusterView extends View {
    private ContentPanel clusterPanel;
    private NodesPanel nodesPanel;
    private ServicesPanel servicesPanel;
    private WatchesPanel watchesPanel;

    public ClusterView(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleEvent(AppEvent event) {
        if (event.getType() == AppEvents.NAV_CLUSTERS) {
            initUI();
        } else if (event.getType() == AppEvents.VIEW_CLUSTER) {
            Cluster cluster = event.getData();
            onShowCluster(cluster);
        } else if (event.getType() == AppEvents.VIEW_NODES) {
            Log.debug("Received event with data " + event.getData());
            Log.debug("Watches " + event.getData("watches"));
            Map<Node, List<Watch>> watches = event.getData("watches");
            onShowNodes(watches);
        } else if (event.getType() == AppEvents.VIEW_SERVICES) {
            Log.debug("Received event with data " + event.getData());
            Log.debug("Watches " + event.getData("watches"));
            Map<Service, List<Watch>> watches = event.getData("watches");
            onShowServices(watches);
        } else if (event.getType() == AppEvents.UPDATE_WATCH) {
            Log.debug("Event has data " + event.getData());
            Log.debug("Event has watches " + event.getData("watches"));
            String watchName = event.getData();
            Map<Node, Watch> watches = event.getData("watches");
            onShowWatch(watchName, watches);
        }
    }

    private void onShowCluster(Cluster cluster) {
        if (cluster != null) {
            clusterPanel.setHeading("Cluster: " + cluster.getName());

            List<Node> nodes = cluster.getNodes();
            if (nodes != null) {
                nodesPanel.getStore().add(nodes);
                nodesPanel.getGrid().getSelectionModel().addSelectionChangedListener(
                        new SelectionChangedListener<Node>() {
                            @Override
                            public void selectionChanged(SelectionChangedEvent<Node> se) {
                                Dispatcher.forwardEvent(AppEvents.VIEW_NODES, se.getSelection());
                            }
                        });
            }

            List<Application> applications = cluster.getApplications();
            if (applications != null) {
                Log.debug("Found " + applications.size() + " applications");
                for (Application application : applications) {
                    if (application.getServices() == null)
                        continue;
                    Log.debug("Adding services from application " + application.getName());
                    servicesPanel.getStore().add(application.getServices());
                }
                servicesPanel.getGrid().getSelectionModel().addSelectionChangedListener(
                        new SelectionChangedListener<Service>() {
                            @Override
                            public void selectionChanged(SelectionChangedEvent<Service> se) {
                                Dispatcher.forwardEvent(AppEvents.VIEW_SERVICES, se.getSelection());
                            }
                        });
            }

            clusterPanel.layout();
        }
    }

    private void onShowNodes(Map<Node, List<Watch>> watches) {
        if (watches == null || watches.size() == 0) {
            Log.warn("No watch found for the selected nodes!");
            return;
        }
        watchesPanel.removeAllGraphPanels();

        // add all graph panels
        for (Node node : watches.keySet()) {
            watchesPanel.addGraphPanelForNode(node);
        }

        // flatten watches list
        List<Watch> flattened = new ArrayList<Watch>();
        for (List<Watch> ws : watches.values()) {
            for (Watch w : ws) {
                if (!flattened.contains(w))
                    flattened.add(w);
            }
        }
        watchesPanel.onWatchesChange(Type.NODE, flattened);

        // layout
        watchesPanel.layout();
    }

    private void onShowServices(Map<Service, List<Watch>> watches) {
        if (watches == null || watches.size() == 0) {
            Log.warn("No watch found for the selected services!");
            return;
        }
        watchesPanel.removeAllGraphPanels();

        // add all graph panels
        for (Service service : watches.keySet()) {
            watchesPanel.addGraphPanelForService(service);
        }

        // flatten watches list
        List<Watch> flattened = new ArrayList<Watch>();
        for (List<Watch> ws : watches.values()) {
            for (Watch w : ws) {
                if (!flattened.contains(w))
                    flattened.add(w);
            }
        }
        watchesPanel.onWatchesChange(Type.SERVICE, flattened);

        // layout
        watchesPanel.layout();
    }

    private void onShowWatch(String watchName, Map<Node, Watch> watches) {
        Log.debug("Displaying data from watch " + watchName);
        watchesPanel.onWatchesDataUpdate(watches);
    }

    protected void initUI() {
        LayoutContainer container = (LayoutContainer) Registry.get(ClustersView.CENTER);
        container.removeAll();

        container.setLayout(new FitLayout());
        container.setBorders(false);

        clusterPanel = new ContentPanel();
        clusterPanel.setLayout(new BorderLayout());
        clusterPanel.setBorders(false);
        clusterPanel.setHeaderVisible(true);
        clusterPanel.setHeading("Cluster");

        nodesPanel = new NodesPanel();
        servicesPanel = new ServicesPanel();
        watchesPanel = new WatchesPanel();

        LayoutContainer left = new LayoutContainer(new AccordionLayout());
        left.add(nodesPanel);
        left.add(servicesPanel);
        left.layout();

        BorderLayoutData leftData = new BorderLayoutData(LayoutRegion.WEST, 300);
        leftData.setCollapsible(true);
        leftData.setSplit(true);
        BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
        centerData.setCollapsible(true);
        centerData.setSplit(true);
        clusterPanel.add(left, leftData);
        clusterPanel.add(watchesPanel, centerData);
        clusterPanel.layout();

        container.add(clusterPanel);

        container.layout();
    }

}
