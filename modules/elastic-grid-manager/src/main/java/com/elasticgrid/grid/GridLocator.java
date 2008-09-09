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

import com.elasticgrid.model.Node;
import com.elasticgrid.model.GridException;
import com.elasticgrid.model.GridNotFoundException;
import java.util.List;

public interface GridLocator <N extends Node> {

    /**
     * Locate all grids.
     * @return the grids name.
     * @throws com.elasticgrid.model.GridException if tehre is a technical error
     */
    List<String> findGrids() throws GridException;

    /**
     * Locate nodes which are part of a grid.
     * @param gridName the name of the grid for whom nodes should be found
     * @return the list of {@link Node}s
     * @throws com.elasticgrid.model.GridNotFoundException if the grid can't be found
     * @throws GridException if there is a technical error
     */
    List<N> findNodes(String gridName) throws GridNotFoundException, GridException;

    /**
     * Locate a monitor instance in the specified grid.
     * @param gridName the name of the grid for whom a monitor instance should be found
     * @return a monitor {@link Node}
     * @throws GridNotFoundException if the grid can't be found
     * @throws GridException if there is a technical error
     */
    N findMonitor(String gridName) throws GridNotFoundException, GridException;
}
