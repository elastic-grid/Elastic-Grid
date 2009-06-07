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

package com.elasticgrid.tools.ui;

import com.elasticgrid.utils.amazon.AWSUtils;
import com.elasticgrid.cluster.ClusterManager;
import com.elasticgrid.cluster.CloudFederationClusterManager;
import com.elasticgrid.cluster.spi.CloudPlatformManager;
import com.elasticgrid.cluster.discovery.ClusterLocator;
import com.elasticgrid.model.lan.LANCluster;
import com.elasticgrid.model.ec2.EC2Cluster;
import com.elasticgrid.platforms.ec2.EC2CloudPlatformManagerFactory;
import com.elasticgrid.platforms.lan.LANCloudPlatformManagerFactory;
import java.io.IOException;
import java.util.Arrays;

public class Main {

    public Main() throws IOException {
        if (!AWSUtils.isEnvironmentProperlySet()) {
            System.err.println("Missing eg.properties file in $EG_HOME/config");
            System.exit(-1);    // todo: instead provide a UI for entering this information and keep going
        }
        // setup Spring Application Context
        ClusterManager clusterManager = getClusterManager();
        ClusterLocator clusterLocator = new EC2CloudPlatformManagerFactory().getClusterLocator();
        // build the initial UI
        new MainPanel(clusterManager, clusterLocator);
    }

    private ClusterManager getClusterManager() throws IOException {
        CloudFederationClusterManager clusterManager = new CloudFederationClusterManager();
        CloudPlatformManager<LANCluster> lanCloud = new LANCloudPlatformManagerFactory().getInstance();
        CloudPlatformManager<EC2Cluster> ec2Cloud = new EC2CloudPlatformManagerFactory().getInstance();
        clusterManager.setClouds(Arrays.asList(lanCloud, ec2Cloud));
        return clusterManager;
    }

    public static void main(String[] args) throws IOException {
        new Main();
    }
}
