package com.elasticgrid.tools.cli.grid;

import com.elasticgrid.grid.ec2.GridNotFoundException;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import java.rmi.RemoteException;
import java.util.List;
import static java.lang.String.format;

/**
 * Resize the grid so that it shrinks or grows to the specified size.
 * @author Jerome Bernard
 */
public class ResizeGridCommand extends AbstractGridCommand {
    @Argument(index = 0, usage = "New size of the grid", required = true)
    private int newSize;

    void execute(String gridName, List<String> args) throws RemoteException {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args.toArray(new String[args.size()]));
        } catch (CmdLineException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        try {
            gridManager.resizeGrid(gridName, newSize);
        } catch (GridNotFoundException e) {
            logger.error(format("Can't find grid '%s'", gridName));
        }
    }
}
