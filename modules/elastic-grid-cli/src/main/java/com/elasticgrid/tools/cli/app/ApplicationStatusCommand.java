package com.elasticgrid.tools.cli.app;

import java.util.List;

/**
 * Display the status of the application on the grid.
 * @author Jerome Bernard
 */
public class ApplicationStatusCommand extends AbstractApplicationCommand {
    void execute(String applicationName, List<String> args) {
        String gridName = args.get(0);
        System.out.printf("Displaying status of application %s on grid %s\n", applicationName, gridName);
    }
}