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
import com.elasticgrid.admin.client.widget.storage.StorageEnginesPanel;
import com.elasticgrid.admin.model.StorageEngine;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import java.util.List;

public class StorageEnginesView extends View {
    private StorageEnginesPanel storageEnginesPanel;

    public StorageEnginesView(Controller controller) {
        super(controller);
    }

    protected void initUI() {
        TabItem west = (TabItem) Registry.get(AppView.TAB_STORAGE);
        ContentPanel clusters = new ContentPanel();
        clusters.setAnimCollapse(false);
        clusters.setHeading("Storage Engines");
        clusters.addListener(Events.Expand, new Listener<ComponentEvent>() {
            public void handleEvent(ComponentEvent be) {
                Dispatcher.get().dispatch(AppEvents.NAV_STORAGE);
            }
        });

        storageEnginesPanel = new StorageEnginesPanel();
        storageEnginesPanel.getGrid().getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<StorageEngine>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<StorageEngine> se) {
                AppEvent evt = new AppEvent(AppEvents.VIEW_STORAGE_ENGINE, se.getSelectedItem());
                Dispatcher.forwardEvent(evt);
            }
        });
        clusters.add(storageEnginesPanel, new BorderLayoutData(LayoutRegion.CENTER));

        west.add(clusters);
    }

    @Override
    protected void handleEvent(AppEvent event) {
        if (event.getType() == AppEvents.INIT) {
            initUI();
        } else if (event.getType() == AppEvents.NAV_STORAGE) {
            List<StorageEngine> engines = event.getData();
            if (engines != null) {
                storageEnginesPanel.getStore().add(engines);
            }
        }
    }

}
