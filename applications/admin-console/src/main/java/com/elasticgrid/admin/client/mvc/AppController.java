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
package com.elasticgrid.admin.client.mvc;

import com.elasticgrid.admin.client.App;
import com.elasticgrid.admin.client.AppEvents;
import com.elasticgrid.admin.client.ClusterManagerServiceAsync;
import com.elasticgrid.admin.client.StorageManagerServiceAsync;
import com.elasticgrid.admin.model.Cluster;
import com.elasticgrid.admin.model.StorageEngine;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.List;

public class AppController extends Controller {
    private AppView appView;
    private ClusterManagerServiceAsync clusterManager;
    private StorageManagerServiceAsync storageManager;

    public AppController() {
        registerEventTypes(AppEvents.INIT);
        registerEventTypes(AppEvents.LOGIN);
        registerEventTypes(AppEvents.ERROR);
    }

    @Override
    public void handleEvent(AppEvent event) {
        EventType type = event.getType();
        if (type == AppEvents.INIT) {
            onInit(event);
        } else if (type == AppEvents.LOGIN) {
            onLogin(event);
        } else if (type == AppEvents.ERROR) {
            onError(event);
        }
    }

    @Override
    public void initialize() {
        appView = new AppView(this);
    }

    protected void onError(AppEvent ae) {
        System.err.println("Error: " + ae.<Object>getData());
    }

    private void onInit(AppEvent event) {
        forwardToView(appView, event);
        clusterManager = (ClusterManagerServiceAsync) Registry.get(App.CLUSTER_MANAGER);
        storageManager = (StorageManagerServiceAsync) Registry.get(App.STORAGE_MANAGER);

        clusterManager.findClusters(new AsyncCallback<List<Cluster>>() {
            public void onSuccess(List<Cluster> clusters) {
                Dispatcher.forwardEvent(AppEvents.NAV_CLUSTERS, clusters);
            }
            public void onFailure(Throwable throwable) {
                Dispatcher.forwardEvent(AppEvents.ERROR, throwable);
            }
        });
        storageManager.getAvailableStorageEngines(new AsyncCallback<List<StorageEngine>>() {
            public void onSuccess(List<StorageEngine> storageEngines) {
                Dispatcher.forwardEvent(AppEvents.NAV_STORAGE, storageEngines);
            }
            public void onFailure(Throwable throwable) {
                Dispatcher.forwardEvent(AppEvents.ERROR, throwable);
            }
        });
    }

    private void onLogin(AppEvent event) {
        forwardToView(appView, event);
    }

}
