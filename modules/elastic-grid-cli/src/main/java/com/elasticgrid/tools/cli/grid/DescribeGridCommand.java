package com.elasticgrid.tools.cli.grid;

import java.util.List;
import java.rmi.RemoteException;

/**
 * Describe a grid
 * @author Jerome Bernard
 */
public class DescribeGridCommand extends AbstractGridCommand {
    public void execute(String gridName, List<String> args) throws RemoteException {
        // todo: repositoryManager.describeGrid(gridName);
    }
}