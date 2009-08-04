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
package com.elasticgrid.platforms.rackspace;

import com.elasticgrid.cluster.discovery.ClusterLocator;
import com.elasticgrid.cluster.spi.CloudPlatformManagerFactory;
import com.elasticgrid.config.EC2Configuration;
import com.elasticgrid.config.RackspaceConfiguration;
import com.elasticgrid.model.rackspace.CloudServersCluster;
import com.elasticgrid.platforms.lan.discovery.JiniGroupsClusterLocator;
import com.elasticgrid.utils.amazon.AWSUtils;
import com.elasticgrid.utils.rackspace.RackspaceUtils;
import net.elasticgrid.rackspace.cloudservers.CloudServers;
import net.elasticgrid.rackspace.cloudservers.XMLCloudServers;
import net.elasticgrid.rackspace.common.RackspaceException;
import java.io.IOException;
import java.util.Properties;

public class CloudServersCloudPlatformManagerFactory implements CloudPlatformManagerFactory<CloudServersCluster> {
    static CloudServersCloudPlatformManager instance;
    static ClusterLocator clusterLocator;
    static CloudServersNodeInstantiator nodeInstantiator;
    static CloudServers cs;

    public CloudServersCloudPlatformManager getInstance() throws IOException, RackspaceException {
        if (instance == null) {
            Properties config = AWSUtils.loadEC2Configuration();

            String username = config.getProperty(RackspaceConfiguration.LOGIN);
            String apiKey = config.getProperty(RackspaceConfiguration.API_KEY);

            instance = new CloudServersCloudPlatformManager();
            instance.setOverridesBucket(config.getProperty(EC2Configuration.EG_OVERRIDES_BUCKET));
            instance.setUsername(username);
            instance.setApiKey(apiKey);
            instance.setImage(config.getProperty(RackspaceConfiguration.CLOUD_SERVERS_IMAGE));
            instance.setClusterLocator(getClusterLocator());
            instance.setNodeInstantiator(getNodeInstantiator());
        }

        return instance;
    }

    public ClusterLocator getClusterLocator() throws IOException {
        if (clusterLocator == null) {
            clusterLocator = new JiniGroupsClusterLocator();
//            clusterLocator.setEc2(getEC2());
        }
        return clusterLocator;
    }

    public CloudServersNodeInstantiator getNodeInstantiator() throws IOException, RackspaceException {
        if (nodeInstantiator == null) {
            nodeInstantiator = new CloudServersNodeInstantiatorImpl();
            ((CloudServersNodeInstantiatorImpl) nodeInstantiator).setCloudServers(getCloudServers());
        }
        return nodeInstantiator;
    }

    private CloudServers getCloudServers() throws IOException, RackspaceException {
        if (cs == null) {
            Properties config = RackspaceUtils.loadRackspaceConfiguration();
            String username = config.getProperty(RackspaceConfiguration.LOGIN);
            String apiKey = config.getProperty(RackspaceConfiguration.API_KEY);
            cs = new XMLCloudServers(username, apiKey);
        }
        return cs;
    }

}