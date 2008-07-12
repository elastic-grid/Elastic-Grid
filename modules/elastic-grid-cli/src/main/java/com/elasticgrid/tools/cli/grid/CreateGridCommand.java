package com.elasticgrid.tools.cli.grid;

import com.elasticgrid.grid.ec2.GridAlreadyRunningException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import java.rmi.RemoteException;
import java.util.List;
import static java.lang.String.format;

/**
 * Create a new Amazon EC2 grid.
 * @author Jerome Bernard
 */
public class CreateGridCommand extends AbstractGridCommand {
    @Option(name = "size", usage = "Size of the grid to create")
    private int size = 1;

    @Option(name = "type", usage = "Kind of Grid. Currently only: 'ec2'")
    private String type = "ec2";

    public void execute(String gridName, List<String> args) throws RemoteException {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args.toArray(new String[args.size()]));
        } catch (CmdLineException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        try {
            gridManager.startGrid(gridName, size);
        } catch (GridAlreadyRunningException e) {
            logger.error(format("Grid '%s' is already running", gridName));
        }
    }
}
