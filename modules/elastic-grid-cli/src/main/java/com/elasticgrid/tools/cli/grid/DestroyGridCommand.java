package com.elasticgrid.tools.cli.grid;

import com.elasticgrid.grid.ec2.GridNotFoundException;
import java.util.List;
import java.rmi.RemoteException;
import static java.lang.String.format;

/**
 * Destroy an Amazon EC2 grid.
 * @author Jerome Bernard
 */
public class DestroyGridCommand extends AbstractGridCommand {
    public void execute(String gridName, List<String> args) throws RemoteException {
        try {
            gridManager.destroyGrid(gridName);
        } catch (GridNotFoundException e) {
            logger.error(format("Can't destroy grid '%s'", gridName));
        }
    }
}
