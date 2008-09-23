/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This file is part of Elastic Grid.
 * 
 * Elastic Grid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * 
 * Elastic Grid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Elastic Grid.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elasticgrid.tools.ui;

import com.elasticgrid.utils.amazon.AWSUtils;
import com.elasticgrid.grid.GridManager;
import com.elasticgrid.grid.GridLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

    public Main() {
        if (!AWSUtils.isEnvironmentProperlySet()) {
            System.err.println("Missing eg.properties file in $EG_HOME/config");
            System.exit(-1);    // todo: instead provide a UI for entering this information and keep going
        }
        // setup Spring Application Context
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/com/elasticgrid/grid/ec2/applicationContext.xml");
        GridManager gridManager = (GridManager) ctx.getBean("gridManager", GridManager.class);
        GridLocator gridLocator = (GridLocator) ctx.getBean("gridLocator", GridLocator.class);
        // build the initial UI
        new MainPanel(gridManager, gridLocator);
    }

    public static void main(String[] args) {
        new Main();
    }
}
