package com.elasticgrid.grid;

import com.elasticgrid.grid.ec2.GridAlreadyRunningException;
import com.elasticgrid.grid.ec2.GridNotFoundException;
import com.elasticgrid.model.Grid;

import java.rmi.RemoteException;

public interface GridManager<G extends Grid> {
    /**
     * Same as calling {@link #startGrid(String, int)} with a <tt>size</tt> of <tt>1</tt>.
     * @param gridName the name of the grid to start
     * @throws GridAlreadyRunningException if the grid is already running
     * @throws RemoteException if there is a network failure
     * @see #startGrid(String, int)
     */
    void startGrid(String gridName) throws GridAlreadyRunningException, RemoteException;

    void startGrid(String gridName, int size) throws GridAlreadyRunningException, RemoteException;

    void stopGrid(String gridName) throws GridNotFoundException, RemoteException;

    G stopGrid(G grid) throws RemoteException;

    /**
     * Retrieve grid details.
     * @param name the name of the grid
     * @return the grid or null if the grid is not found
     * @throws RemoteException if there is a network failure
     */
    G grid(String name) throws RemoteException;

    void resizeGrid(String gridName, int newSize) throws GridNotFoundException, RemoteException;

    void destroyGrid(String gridName) throws GridNotFoundException, RemoteException;
}
