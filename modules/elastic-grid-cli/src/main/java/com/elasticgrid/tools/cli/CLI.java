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

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Argument;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.rmi.RemoteException;
import java.util.Map;

/**
 * Elastic Grid command-line administration tool.
 * @author Jerome Bernard
 */
public class CLI implements Runnable {
    @Argument(index = 0, usage = "Command to execute", required = true)
    private String section;

    @Argument(index = 1, usage = "Command to execute", required = false)
    private String command;

    @Argument(index = 2, multiValued = true, required = false)
    private String args;

    @Option(name = "-s", usage = "Amazon EC2 secret key", required = false)
    private String secretKey;

    private Map<String, Command> commands;

    private CmdLineParser parser;

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
                "/applicationContext.xml",
                "/com/elasticgrid/repository/applicationContext.xml"
        });
        CLI cli = (CLI) context.getBean("cli");
        cli.parser = new CmdLineParser(cli);
        cli.parse(args);
    }

    public void parse(String... command) {
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            usage(e.getMessage());
        }
        run();
    }

    public void run() {
        String commandKey;
        if (command != null && !"".equals(command))
            commandKey = section + '-' + command;
        else
            commandKey = section;
        Command cmd = commands.get(commandKey);
        if (cmd != null) {
            try {
                cmd.execute(args);
            } catch (IllegalArgumentException e) {
                usage(e.getMessage());
            } catch (RemoteException e) {
                usage(e.getMessage());
            }
        } else {
            usage("Unknown command");
        }
    }

    public void usage(String message) {
        System.err.printf("%s\n, eg section command [options...] [arguments...]\n", message);
        parser.printUsage(System.err);
        System.exit(-1);
    }

    public void setCommands(Map<String, Command> commands) {
        this.commands = commands;
    }
}
