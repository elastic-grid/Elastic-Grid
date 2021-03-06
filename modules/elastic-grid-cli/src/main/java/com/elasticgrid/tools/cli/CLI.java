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
package com.elasticgrid.tools.cli;

import com.elasticgrid.cluster.ClusterManager;
import com.elasticgrid.cluster.CloudFederationClusterManager;
import com.elasticgrid.cluster.spi.CloudPlatformManager;
import com.elasticgrid.model.lan.LANCluster;
import com.elasticgrid.model.ec2.EC2Cluster;
import com.elasticgrid.platforms.ec2.EC2CloudPlatformManagerFactory;
import com.elasticgrid.platforms.lan.LANCloudPlatformManagerFactory;
import com.elasticgrid.storage.StorageManager;
import org.rioproject.tools.cli.DirHandler;
import org.rioproject.tools.cli.ListHandler;
import org.rioproject.tools.cli.MonitorControl;
import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.io.IOException;

public class CLI extends org.rioproject.tools.cli.CLI {
    static CloudFederationClusterManager clusterManager;

    static {
        instance = new CLI();
    }

    protected CLI() {
        super();
        cliName = "Elastic Grid";
        prompt = "eg> ";
    }

    protected void loadOptionHandlers(Configuration config) throws ConfigurationException {
        OptionHandlerDesc[] defaultHandlers =
                new OptionHandlerDesc[]{
                        // Elastic Grid handlers
                        new OptionHandlerDesc("list-clusters", ListClustersHandler.class.getName()),
                        new OptionHandlerDesc("start-cluster", StartClusterHandler.class.getName()),
                        new OptionHandlerDesc("stop-cluster", StopClusterHandler.class.getName()),
                        new OptionHandlerDesc("resize-cluster", ResizeClusterHandler.class.getName()),
                        new OptionHandlerDesc("install", InstallHandler.class.getName()),
                        // Rio handlers
                        new OptionHandlerDesc("list", ListHandler.class.getName()),
                        new OptionHandlerDesc("destroy", StopHandler.class.getName()),
                        new OptionHandlerDesc("deploy", MonitorControl.DeployHandler.class.getName()),
                        new OptionHandlerDesc("redeploy", MonitorControl.RedeployHandler.class.getName()),
                        new OptionHandlerDesc("undeploy", MonitorControl.UndeployHandler.class.getName()),
                        new OptionHandlerDesc("set", SettingsHandler.class.getName()),
                        new OptionHandlerDesc("ls", DirHandler.class.getName()),
                        new OptionHandlerDesc("dir", DirHandler.class.getName()),
                        new OptionHandlerDesc("pwd", DirHandler.class.getName()),
                        new OptionHandlerDesc("cd", DirHandler.class.getName()),
                        new OptionHandlerDesc("jconsole", JConsoleHandler.class.getName()),
                        new OptionHandlerDesc("stats", StatsHandler.class.getName()),
                        new OptionHandlerDesc("http", HttpHandler.class.getName()),
                        new OptionHandlerDesc("help", HelpHandler.class.getName())
                };
        OptionHandlerDesc[] optionHandlers =
                (OptionHandlerDesc[]) config.getEntry(COMPONENT,
                        "optionHandlers", OptionHandlerDesc[].class, defaultHandlers);
        OptionHandlerDesc[] addOptionHandlers =
                (OptionHandlerDesc[]) config.getEntry(COMPONENT,
                        "addOptionHandlers", OptionHandlerDesc[].class, new OptionHandlerDesc[0]);
        if (addOptionHandlers.length > 0) {
            if (logger.isLoggable(Level.CONFIG)) {
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < addOptionHandlers.length; i++) {
                    if (i > 0)
                        buffer.append("\n");
                    buffer.append("    ").append(addOptionHandlers[i].toString());
                }
                logger.config("addOptionHandlers\n" + buffer.toString());
            }
            List<OptionHandlerDesc> list = new ArrayList<OptionHandlerDesc>();
            list.addAll(Arrays.asList(optionHandlers));
            list.addAll(Arrays.asList(addOptionHandlers));
            optionHandlers = list.toArray(new OptionHandlerDesc[list.size()]);
        }

        for (OptionHandlerDesc optionHandler : optionHandlers) {
            optionMap.put(optionHandler.getName(), optionHandler);
        }
    }

    public static ClusterManager getClusterManager() throws IOException {
        if (clusterManager == null) {
            clusterManager = new CloudFederationClusterManager();
            CloudPlatformManager<LANCluster> lanCloud = new LANCloudPlatformManagerFactory().getInstance();
            CloudPlatformManager<EC2Cluster> ec2Cloud = new EC2CloudPlatformManagerFactory().getInstance();
            clusterManager.setClouds(Arrays.asList(lanCloud, ec2Cloud));
        }
        return clusterManager;
    }
}
