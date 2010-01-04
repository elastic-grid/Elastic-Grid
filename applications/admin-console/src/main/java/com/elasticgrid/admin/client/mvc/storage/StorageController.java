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

import com.elasticgrid.admin.client.App;
import com.elasticgrid.admin.client.AppEvents;
import com.elasticgrid.admin.client.StorageManagerServiceAsync;
import com.elasticgrid.admin.model.StorageEngine;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class StorageController extends Controller {
    private StorageManagerServiceAsync service;
    private StorageEnginesView storageEnginesView;
    private StorageEngineView storageEngineView;

    public StorageController() {
        registerEventTypes(AppEvents.INIT);
        registerEventTypes(AppEvents.NAV_STORAGE);
        registerEventTypes(AppEvents.VIEW_STORAGE_ENGINE);
    }

    @Override
    public void handleEvent(final AppEvent event) {
        EventType type = event.getType();
        if (type == AppEvents.INIT) {
            forwardToView(storageEnginesView, event);
//        } else if (type == AppEvents.NAV_STORAGE) {
//            forwardToView(storageEnginesView, event);
//            forwardToView(storageEngineView, event);
        } else if (type == AppEvents.VIEW_STORAGE_ENGINE) {
            StorageEngine engine = event.getData();
            final StorageEngine finalEngine = engine;
            service.getStorageEngineDetails(engine.getName(), new AsyncCallback<StorageEngine>() {
                public void onSuccess(StorageEngine storageEngine) {
                    event.setData(finalEngine);
                    forwardToView(storageEngineView, event);
                }
                public void onFailure(Throwable throwable) {
                    Dispatcher.forwardEvent(AppEvents.ERROR, throwable);
                }
            });
        }
    }

    private void onViewStorageEngine(final AppEvent event) {
        final StorageEngine engine = event.getData();
        if (engine != null) {
            AppEvent ae = new AppEvent(event.getType());
            ae.setData("engine", engine);
            forwardToView(storageEngineView, ae);
//            service.findCluster(cluster.getName(), new AsyncCallback<Cluster>() {
//                public void onSuccess(Cluster result) {
//                    AppEvent ae = new AppEvent(event.getType(), result);
//                    ae.setData("cluster", cluster);
//                    forwardToView(clusterView, ae);
//                }
//                public void onFailure(Throwable throwable) {
//                    Dispatcher.forwardEvent(AppEvents.ERROR, throwable);
//                }
//            });
        }
    }

    @Override
    public void initialize() {
        service = (StorageManagerServiceAsync) Registry.get(App.STORAGE_MANAGER);
        storageEnginesView = new StorageEnginesView(this);
        storageEngineView = new StorageEngineView(this);
    }
}