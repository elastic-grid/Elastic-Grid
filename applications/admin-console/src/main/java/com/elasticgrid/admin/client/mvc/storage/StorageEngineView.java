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
package com.elasticgrid.admin.client.mvc.storage;

import com.elasticgrid.admin.client.AppEvents;
import com.elasticgrid.admin.client.mvc.AppView;
import com.elasticgrid.admin.client.widget.storage.StorageEnginePanel;
import com.elasticgrid.admin.model.StorageEngine;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class StorageEngineView extends View {
    private StorageEnginePanel storageEnginePanel;

    public StorageEngineView(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleEvent(AppEvent event) {
        if (event.getType() == AppEvents.NAV_STORAGE) {
            initUI();
        } else if (event.getType() == AppEvents.VIEW_STORAGE_ENGINE) {
            StorageEngine engine = event.getData();
            storageEnginePanel.setHeading("Storage Engine: " + engine.getName());
            storageEnginePanel.showStorageEngine(engine);
        }
    }

    protected void initUI() {
        LayoutContainer container = new LayoutContainer();
        FitLayout layout = new FitLayout();
//        layout.setEnableState(false);
        container.setLayout(layout);
        container.setBorders(false);

        storageEnginePanel = new StorageEnginePanel();
        storageEnginePanel.setBorders(false);
        storageEnginePanel.setHeaderVisible(true);
        storageEnginePanel.setHeading("Storage Engine");
        container.add(storageEnginePanel);

        TabItem wrapper = (TabItem) Registry.get(AppView.TAB_STORAGE);
        wrapper.removeAll();
        wrapper.add(container);
        wrapper.layout();
    }

}