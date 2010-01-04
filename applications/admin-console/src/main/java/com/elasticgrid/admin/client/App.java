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
package com.elasticgrid.admin.client;

import com.elasticgrid.admin.client.mvc.AppController;
import com.elasticgrid.admin.client.mvc.cluster.ClustersController;
import com.elasticgrid.admin.client.mvc.storage.StorageController;
import com.extjs.gxt.themes.client.Slate;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.util.ThemeManager;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.allen_sauer.gwt.log.client.Log;

public class App implements EntryPoint {
    public static final String CLUSTER_MANAGER = "clusterManager";
    public static final String STORAGE_MANAGER = "storageManager";

    public void onModuleLoad() {
        // install an UncaughtExceptionHandler which will produce <code>FATAL</code> log messages
        Log.setUncaughtExceptionHandler();

        // use a deferred command so that the UncaughtExceptionHandler catches any exceptions in onModuleLoad2()
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                onModuleLoad2();
            }
        });
    }

    public void onModuleLoad2() {
        GXT.setDefaultTheme(Slate.SLATE, false);
        ThemeManager.register(Slate.SLATE);

        // register cluster manager service
        ClusterManagerServiceAsync clusterManager = (ClusterManagerServiceAsync) GWT.create(ClusterManagerService.class);
        ((ServiceDefTarget) clusterManager).setServiceEntryPoint(CLUSTER_MANAGER);
        Registry.register(CLUSTER_MANAGER, clusterManager);

        // register storage manager service
        StorageManagerServiceAsync storageManager = (StorageManagerServiceAsync) GWT.create(StorageManagerService.class);
        ((ServiceDefTarget) storageManager).setServiceEntryPoint(STORAGE_MANAGER);
        Registry.register(STORAGE_MANAGER, storageManager);

        // register controllers
        Dispatcher dispatcher = Dispatcher.get();
        dispatcher.addController(new AppController());
        dispatcher.addController(new ClustersController());
        dispatcher.addController(new StorageController());

        // dispatch login event
        dispatcher.dispatch(AppEvents.LOGIN);

        // hide loading panel
        GXT.hideLoadingPanel("loading");
    }
}
