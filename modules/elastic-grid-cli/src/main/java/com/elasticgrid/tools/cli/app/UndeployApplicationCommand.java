package com.elasticgrid.tools.cli.app;

import java.util.List;

/**
 * Uneploy an application from the grid.
 * @author Jerome Bernard
 */
public class UndeployApplicationCommand extends AbstractApplicationCommand {
    void execute(String applicationName, List<String> args) {
        String gridName = args.get(0);
        System.out.printf("Undeploying application %s on grid %s\n", applicationName, gridName);
    }
}