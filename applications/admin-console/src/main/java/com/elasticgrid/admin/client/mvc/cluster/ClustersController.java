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
import com.elasticgrid.admin.client.App;
import com.elasticgrid.admin.client.AppEvents;
import com.elasticgrid.admin.client.ClusterManagerServiceAsync;
import com.elasticgrid.admin.model.Cluster;
import com.elasticgrid.admin.model.Node;
import com.elasticgrid.admin.model.Service;
import com.elasticgrid.admin.model.Watch;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClustersController extends Controller {
    private ClusterManagerServiceAsync service;
    private ClustersView clustersView;
    private ClusterView clusterView;
    private Timer watchUpdater;
    private Command watchUpdateCommand;
    private int watchUpdateSpeed = 10 * 1000;        // every 10 seconds

    public ClustersController() {
        registerEventTypes(AppEvents.INIT);
        registerEventTypes(AppEvents.NAV_CLUSTERS);
        registerEventTypes(AppEvents.VIEW_CLUSTER);
        registerEventTypes(AppEvents.VIEW_NODES);
        registerEventTypes(AppEvents.VIEW_SERVICES);
        registerEventTypes(AppEvents.VIEW_WATCH);
        registerEventTypes(AppEvents.UPDATE_WATCH);
        registerEventTypes(AppEvents.START_CLUSTER);
        registerEventTypes(AppEvents.STOP_CLUSTER);
        registerEventTypes(AppEvents.START_NODE);
        registerEventTypes(AppEvents.STOP_NODE);
        registerEventTypes(AppEvents.DEPLOY_APPLICATION);
    }

    @Override
    public void handleEvent(AppEvent event) {
        EventType type = event.getType();
        if (type == AppEvents.INIT) {
            forwardToView(clustersView, event);
        } else if (type == AppEvents.NAV_CLUSTERS) {
            cancelWatchUpdater();
            forwardToView(clustersView, event);
            forwardToView(clusterView, event);
        } else if (type == AppEvents.VIEW_CLUSTER) {
            cancelWatchUpdater();
            onViewCluster(event);
        } else if (type == AppEvents.VIEW_NODES) {
            cancelWatchUpdater();
            onViewNodes(event);
        } else if (type == AppEvents.VIEW_SERVICES) {
            cancelWatchUpdater();
            onViewServices(event);
        } else if (type == AppEvents.VIEW_WATCH) {
            cancelWatchUpdater();
            onViewWatch(event);
        } else if (type == AppEvents.START_CLUSTER) {
            cancelWatchUpdater();
            onStartCluster(event);
        } else if (type == AppEvents.STOP_CLUSTER) {
            cancelWatchUpdater();
            // TODO: write this!
        } else if (type == AppEvents.START_NODE) {
            cancelWatchUpdater();
            // TODO: write this!
        } else if (type == AppEvents.STOP_NODE) {
            cancelWatchUpdater();
            onStopNode(event);
        } else if (type == AppEvents.DEPLOY_APPLICATION) {
            onDeployApplication(event);
        }
    }

    private void onViewCluster(final AppEvent event) {
        final Cluster cluster = event.getData();
        if (cluster != null) {
            service.findCluster(cluster.getName(), new AsyncCallback<Cluster>() {
                public void onSuccess(Cluster result) {
                    AppEvent ae = new AppEvent(event.getType(), result);
                    ae.setData("cluster", result);
                    forwardToView(clusterView, ae);
                }

                public void onFailure(Throwable throwable) {
                    Dispatcher.forwardEvent(AppEvents.ERROR, throwable);
                }
            });
        }
    }

    private void onViewNodes(final AppEvent event) {
        final List<Node> nodes = event.getData();
        if (nodes != null && nodes.size() > 0) {
            service.getWatchesForNodes(nodes, new AsyncCallback<Map<Node, List<Watch>>>() {
                public void onSuccess(Map<Node, List<Watch>> result) {
                    AppEvent ae = new AppEvent(event.getType(), nodes);
                    ae.setData("watches", result);
                    forwardToView(clusterView, ae);
                }

                public void onFailure(Throwable throwable) {
                    Dispatcher.forwardEvent(AppEvents.ERROR, throwable);
                }
            });
        }
    }

    private void onViewServices(final AppEvent event) {
        final List<Service> services = event.getData();
        if (services != null && services.size() > 0) {
            service.getWatchesForServices(services, new AsyncCallback<Map<Service, List<Watch>>>() {
                public void onSuccess(Map<Service, List<Watch>> result) {
                    AppEvent ae = new AppEvent(event.getType(), services);
                    ae.setData("watches", result);
                    forwardToView(clusterView, ae);
                }

                public void onFailure(Throwable throwable) {
                    Dispatcher.forwardEvent(AppEvents.ERROR, throwable);
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    private void onViewWatch(final AppEvent event) {
        final String watchName = event.getData();
        Object nodesData = event.getData("nodes");
        Object servicesData = event.getData("services");
        final List<Node> nodes;
        final List<Service> services;
        if (nodesData != null)
            nodes = new ArrayList<Node>((Set<Node>) nodesData);
        else
            nodes = null;
        if (servicesData != null)
            services = new ArrayList<Service>((Set<Service>) servicesData);
        else
            services = null;
        if (watchName != null) {
            if (nodes != null) {
                watchUpdateCommand = new Command() {
                    public void execute() {
                        service.getWatchOnEachNode(nodes, watchName, new AsyncCallback<Map<Node, Watch>>() {
                            public void onSuccess(Map<Node, Watch> result) {
                                AppEvent ae = new AppEvent(AppEvents.UPDATE_WATCH, watchName);
                                ae.setData("watches", result);
                                forwardToView(clusterView, ae);
                            }

                            public void onFailure(Throwable throwable) {
                                Dispatcher.forwardEvent(AppEvents.ERROR, throwable);
                            }
                        });
                    }
                };
            } else if (services != null) {
                watchUpdateCommand = new Command() {
                    public void execute() {
                        service.getWatchOnEachService(services, watchName, new AsyncCallback<Map<Service, Watch>>() {
                            public void onSuccess(Map<Service, Watch> result) {
                                AppEvent ae = new AppEvent(AppEvents.UPDATE_WATCH, watchName);
                                ae.setData("watches", result);
                                forwardToView(clusterView, ae);
                            }

                            public void onFailure(Throwable throwable) {
                                Dispatcher.forwardEvent(AppEvents.ERROR, throwable);
                            }
                        });
                    }
                };
            }
            adjustWatchUpdateSpeed(watchUpdateSpeed);
        }
    }

    private void onStartCluster(final AppEvent event) {
        Log.info("Should start cluster " + event.getData("clusterName"));
        final Node node = event.getData();
        if (node != null) {
            service.stopNode(node, new AsyncCallback<Void>() {
                public void onSuccess(Void result) {
                    AppEvent ae = new AppEvent(event.getType(), result);
                    ae.setData("node", node);
                    forwardToView(clusterView, ae);
                }

                public void onFailure(Throwable throwable) {
                    Dispatcher.forwardEvent(AppEvents.ERROR, throwable);
                }
            });
        }
    }

    private void onStopNode(final AppEvent event) {
        final Node node = event.getData();
        if (node != null) {
            service.stopNode(node, new AsyncCallback<Void>() {
                public void onSuccess(Void result) {
                    AppEvent ae = new AppEvent(event.getType(), result);
                    ae.setData("node", node);
                    forwardToView(clusterView, ae);
                }

                public void onFailure(Throwable throwable) {
                    Dispatcher.forwardEvent(AppEvents.ERROR, throwable);
                }
            });
        }
    }

    private void onDeployApplication(final AppEvent event) {
        Log.info("Should deploy the application...");
        service.deployApplication(event.<String>getData(), new AsyncCallback<Void>() {
            public void onSuccess(Void result) {
                // TODO: not sure what to do here?
            }
            public void onFailure(Throwable throwable) {
                Dispatcher.forwardEvent(AppEvents.ERROR, throwable);
            }
        });
    }

    private void cancelWatchUpdater() {
        if (watchUpdater != null)
            watchUpdater.cancel();
    }

    private void adjustWatchUpdateSpeed(int newSpeed) {
        watchUpdateSpeed = newSpeed;
        watchUpdateCommand.execute();

        if (watchUpdater != null) watchUpdater.cancel();
        if (watchUpdateSpeed == 0) {
            return;
        }
        watchUpdater = new Timer() {
            @Override
            public void run() {
                watchUpdateCommand.execute();
            }
        };
        watchUpdater.scheduleRepeating(watchUpdateSpeed);
    }

    @Override
    public void initialize() {
        service = (ClusterManagerServiceAsync) Registry.get(App.CLUSTER_MANAGER);
        clustersView = new ClustersView(this);
        clusterView = new ClusterView(this);
    }
}
