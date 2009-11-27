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
package com.elasticgrid.admin.client;

import com.elasticgrid.admin.model.Cluster;
import com.elasticgrid.admin.model.Node;
import com.elasticgrid.admin.model.NodeProfileInfo;
import com.elasticgrid.admin.model.Service;
import com.elasticgrid.admin.model.Watch;
import com.google.gwt.user.client.rpc.RemoteService;
import java.io.File;
import java.util.List;
import java.util.Map;

public interface ClusterManagerService extends RemoteService {
    List<Cluster> findClusters();
    Cluster findCluster(String name);

    void startCluster(String clusterName, List<NodeProfileInfo> clusterTopology);
    void stopCluster(String clusterName);
    void stopNode(Node node);

    /**
     * Deploy an application.
     * @param application the path to the file (OAR) of the application
     */
    void deployApplication(String application);

    Map<Node, List<Watch>> getWatchesForNodes(List<Node> nodes);
    Map<Service, List<Watch>> getWatchesForServices(List<Service> services);
    Map<Node, Watch> getWatchOnEachNode(List<Node> nodes, String id);
    Map<Service, Watch> getWatchOnEachService(List<Service> services, String id);
}
