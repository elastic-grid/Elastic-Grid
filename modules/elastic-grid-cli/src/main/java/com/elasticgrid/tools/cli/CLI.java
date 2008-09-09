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

package com.elasticgrid.tools.cli;

import com.elasticgrid.grid.GridManager;
import org.rioproject.tools.cli.DirHandler;
import org.rioproject.tools.cli.ListHandler;
import org.rioproject.tools.cli.MonitorControl;
import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CLI extends org.rioproject.tools.cli.CLI {
    private static ApplicationContext ctx;

    static {
//        SLF4JBridgeHandler.install();
        instance = new CLI();
        ctx = new ClassPathXmlApplicationContext("/com/elasticgrid/tools/cli/applicationContext.xml");
    }

    protected CLI() {
        super();
        cliName = "Elastic Grid";
        prompt = "eg> ";
    }

    protected void loadOptionHandlers(Configuration config) throws ConfigurationException {
        List<OptionHandlerDesc> optionHandlers = new LinkedList<OptionHandlerDesc>();
        optionHandlers.addAll(Arrays.asList(
                new OptionHandlerDesc("list-grids", ListGridsHandler.class.getName()),
                new OptionHandlerDesc("start-grid", StartGridHandler.class.getName()),
                new OptionHandlerDesc("stop-grid", StopGridHandler.class.getName()),
                new OptionHandlerDesc("resize-grid", ResizeGridHandler.class.getName()),
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
        ));
        for (OptionHandlerDesc optionHandler : optionHandlers) {
            optionMap.put(optionHandler.getName(), optionHandler);
        }
    }

    public static GridManager getGridManager() {
        return (GridManager) ctx.getBean("gridManager");
    }
}
