/**
 * Elastic Grid
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
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
import com.elasticgrid.cluster.discovery.ClusterLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

    public Main() {
        if (!AWSUtils.isEnvironmentProperlySet()) {
            System.err.println("Missing eg.properties file in $EG_HOME/config");
            System.exit(-1);    // todo: instead provide a UI for entering this information and keep going
        }
        // setup Spring Application Context
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/com/elasticgrid/cluster/applicationContext.xml");
        ClusterManager clusterManager = (ClusterManager) ctx.getBean("clusterManager", ClusterManager.class);
        ClusterLocator clusterLocator = (ClusterLocator) ctx.getBean("clusterLocator", ClusterLocator.class);
        // build the initial UI
        new MainPanel(clusterManager, clusterLocator);
    }

    public static void main(String[] args) {
        new Main();
    }
}
