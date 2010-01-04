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

import com.elasticgrid.admin.model.Cluster;
import com.elasticgrid.admin.model.Node;
import com.elasticgrid.admin.model.NodeProfileInfo;
import com.elasticgrid.admin.model.Watch;
import com.elasticgrid.admin.model.Service;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.io.File;
import java.util.List;
import java.util.Map;

public interface ClusterManagerServiceAsync {
    void findClusters(AsyncCallback<List<Cluster>> async);
    void findCluster(String name, AsyncCallback<Cluster> async);
    void startCluster(String clusterName, List<NodeProfileInfo> clusterTopology, AsyncCallback<Void> async);
    void stopCluster(String clusterName, AsyncCallback<Void> async);
    void deployApplication(String application, AsyncCallback<Void> async);
    void stopNode(Node node, AsyncCallback<Void> async);
    void getWatchesForNodes(List<Node> nodes, AsyncCallback<Map<Node, List<Watch>>> async);
    void getWatchesForServices(List<Service> services, AsyncCallback<Map<Service, List<Watch>>> async);
    void getWatchOnEachNode(List<Node> nodes, String id, AsyncCallback<Map<Node,Watch>> async);
    void getWatchOnEachService(List<Service> nodes, String id, AsyncCallback<Map<Service,Watch>> async);
}
