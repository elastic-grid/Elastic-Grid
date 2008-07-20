/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * Licensed under the GNU Lesser General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.elasticgrid.tools.cli;

import com.elasticgrid.grid.GridManager;
import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import org.rioproject.tools.cli.DirHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CLI extends org.rioproject.tools.cli.CLI {
    private static ApplicationContext ctx = new ClassPathXmlApplicationContext("/com/elasticgrid/tools/cli/applicationContext.xml");

    static {
        instance = new CLI();
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
//              new OptionHandlerDesc("list", ListHandler.class.getName()),
//              new OptionHandlerDesc("destroy", StopHandler.class.getName()),
//              new OptionHandlerDesc("deploy", MonitorControl.DeployHandler.class.getName()),
//              new OptionHandlerDesc("redeploy", MonitorControl.RedeployHandler.class.getName()),
//              new OptionHandlerDesc("undeploy", MonitorControl.UndeployHandler.class.getName()),
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
