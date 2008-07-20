/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * Licensed under the GNU Lesser General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.elasticgrid.grid;

import com.elasticgrid.model.GridAlreadyRunningException;
import com.elasticgrid.model.GridNotFoundException;
import com.elasticgrid.model.Grid;
import com.elasticgrid.model.GridException;

import java.rmi.RemoteException;
import java.util.List;

public interface GridManager<G extends Grid> {
    /**
     * Start a grid with a specified name.
     * Same as calling {@link #startGrid(String, int)} with a <tt>size</tt> of <tt>1</tt>.
     * @param gridName the name of the grid to start
     * @throws GridAlreadyRunningException if the grid is already running
     * @throws RemoteException if there is a network failure
     * @see #startGrid(String, int)
     */
    void startGrid(String gridName) throws GridException, RemoteException;

    /**
     * Start a grid with a specified name and a specified number of instances.
     * @param gridName the name of the grid to start
     * @param size the number of nodes to start for this grid
     * @throws GridAlreadyRunningException if the grid is already running
     * @throws RemoteException if there is a network failure
     * @see #startGrid(String)
     */
    void startGrid(String gridName, int size) throws GridException, RemoteException;

    void stopGrid(String gridName) throws GridException, RemoteException;

    /**
     * Retreive all grids details.
     * @return the grids details
     * @throws RemoteException if there is a network failure
     */
    List<Grid> getGrids() throws RemoteException, GridException, RemoteException;

    /**
     * Retrieve grid details.
     * @param name the name of the grid
     * @return the grid or null if the grid is not found
     * @throws RemoteException if there is a network failure
     */
    G grid(String name) throws RemoteException, GridException, RemoteException;

    void resizeGrid(String gridName, int newSize) throws GridNotFoundException, GridException, RemoteException;
}
