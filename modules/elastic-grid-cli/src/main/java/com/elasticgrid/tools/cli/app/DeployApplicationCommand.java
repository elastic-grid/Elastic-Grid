package com.elasticgrid.tools.cli.app;

import java.util.List;

/**
 * Deploy an application on the grid.
 * @author Jerome Bernard
 */
public class DeployApplicationCommand extends AbstractApplicationCommand {
    void execute(String applicationName, List<String> args) {
        String gridName = args.get(0);
        System.out.printf("Deploying application %s on grid %s\n", applicationName, gridName);
    }
}