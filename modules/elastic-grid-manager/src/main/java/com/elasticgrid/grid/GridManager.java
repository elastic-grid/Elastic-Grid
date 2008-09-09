/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This file is part of Elastic Grid.
 * 
 * Elastic Grid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * 
 * Elastic Grid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Elastic Grid.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elasticgrid.grid;

import com.elasticgrid.model.Grid;
import com.elasticgrid.model.GridException;
import com.elasticgrid.model.GridNotFoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface GridManager<G extends Grid> {
    /**
     * Start a grid with a specified name.
     * Same as calling {@link #startGrid(String, int)} with a <tt>size</tt> of <tt>1</tt>.
     * @param gridName the name of the grid to start
     * @throws InterruptedException if the grid was not started before timeout
     * @throws RemoteException if there is a network failure
     * @see #startGrid(String, int)
     */
    void startGrid(String gridName)
            throws GridException, RemoteException, ExecutionException, TimeoutException, InterruptedException;

    /**
     * Start a grid with a specified name and a specified number of instances.
     * @param gridName the name of the grid to start
     * @param size the number of nodes to start for this grid
     * @throws InterruptedException if the grid was not started before timeout
     * @throws RemoteException if there is a network failure
     * @see #startGrid(String)
     */
    void startGrid(String gridName, int size)
            throws GridException, ExecutionException, TimeoutException, InterruptedException, RemoteException;

    void stopGrid(String gridName) throws GridException, RemoteException;

    /**
     * Retreive all grids details.
     * @return the grids details
     * @throws RemoteException if there is a network failure
     */
    List<Grid> getGrids() throws GridException, RemoteException;

    /**
     * Retrieve grid details.
     * @param name the name of the grid
     * @return the grid or null if the grid is not found
     * @throws RemoteException if there is a network failure
     */
    G grid(String name) throws GridException, RemoteException;

    /**
     * Resize a grid.
     * @param gridName the name of the grid to resize
     * @param newSize the new size of the grid
     * @throws GridNotFoundException if the grid can't be found
     * @throws GridException if there is a grid failure
     * @throws InterruptedException if the grid was not started before timeout
     * @throws RemoteException if there is a network failure
     */
    void resizeGrid(String gridName, int newSize)
            throws GridNotFoundException, GridException, ExecutionException, TimeoutException, InterruptedException, RemoteException;
}
