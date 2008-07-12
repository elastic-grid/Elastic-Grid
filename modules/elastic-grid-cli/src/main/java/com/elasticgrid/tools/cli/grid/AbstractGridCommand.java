package com.elasticgrid.tools.cli.grid;

import com.elasticgrid.tools.cli.AbstractCommand;
import com.elasticgrid.repository.RepositoryManager;
import com.elasticgrid.grid.GridManager;

import java.util.Arrays;
import java.util.List;
import java.rmi.RemoteException;

/**
 * @author Jerome Bernard
 */
public abstract class AbstractGridCommand extends AbstractCommand {
    protected GridManager gridManager;

    abstract void execute(String gridName, List<String> args) throws RemoteException;

    public void execute(String... args) throws RemoteException {
        String gridName = args[0];
        execute(gridName, Arrays.asList(args).subList(1, args.length));
    }

    public void setGridManager(GridManager gridManager) {
        this.gridManager = gridManager;
    }
}
