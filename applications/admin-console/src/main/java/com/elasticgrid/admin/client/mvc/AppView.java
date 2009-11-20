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
package com.elasticgrid.admin.client.mvc;

import com.elasticgrid.admin.client.AppEvents;
import com.elasticgrid.admin.client.widget.LoginDialog;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.ui.RootPanel;

public class AppView extends View {
    public static final String VIEWPORT = "viewport";
    public static final String TAB_PANEL = "tab_panel";
    public static final String TAB_CLUSTER = "tab_cluster";
    public static final String TAB_STORAGE = "tab_storage";

    private Viewport viewport;
    private TabPanel tabPanel;
    private TabItem tabCluster, tabStorage;

    public AppView(Controller controller) {
        super(controller);
    }

    @Override
    protected void initialize() {
        LoginDialog dialog = new LoginDialog();
        dialog.setClosable(false);
        dialog.addListener(Events.Hide, new Listener<WindowEvent>() {
            public void handleEvent(WindowEvent be) {
                Dispatcher.forwardEvent(AppEvents.INIT);
            }
        });
        dialog.show();
    }

    private void initUI() {
        viewport = new Viewport();
        viewport.setLayout(new BorderLayout());

        createNorth();
        createCenter();

        // registry serves as a global context
        Registry.register(VIEWPORT, viewport);
        Registry.register(TAB_PANEL, tabPanel);
        Registry.register(TAB_CLUSTER, tabCluster);
        Registry.register(TAB_STORAGE, tabStorage);

        RootPanel.get().add(viewport);
    }

    private void createNorth() {
        StringBuffer sb = new StringBuffer();
        sb.append("<div><img src=\"../images/Elastic-Grid-Logo.png\"/></div>");
        HtmlContainer northPanel = new HtmlContainer(sb.toString());
        northPanel.setStateful(false);
        BorderLayoutData data = new BorderLayoutData(LayoutRegion.NORTH, 65);
        data.setMargins(new Margins());
        viewport.add(northPanel, data);
    }

    private void createCenter() {
        tabPanel = new TabPanel();
        tabPanel.setResizeTabs(true);
        tabPanel.setAnimScroll(true);
        tabPanel.setTabScroll(true);
        tabPanel.setBodyBorder(false);
        tabPanel.setBorders(false);

        tabCluster = new TabItem("Clusters");
        tabCluster.setIcon(IconHelper.createStyle("icon-clusters"));
        tabCluster.setClosable(false);

        tabStorage = new TabItem("Storage");
        tabCluster.setIcon(IconHelper.createStyle("icon-storage"));
        tabStorage.setClosable(false);

        tabPanel.add(tabCluster);
        tabPanel.add(tabStorage);

        BorderLayoutData data = new BorderLayoutData(LayoutRegion.CENTER);
        data.setMargins(new Margins(5, 5, 5, 5));

        viewport.add(tabPanel, data);
    }

    @Override
    protected void handleEvent(AppEvent event) {
        if (event.getType() == AppEvents.INIT) {
            initUI();
        }
    }

}