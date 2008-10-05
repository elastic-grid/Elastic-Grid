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

package com.elasticgrid.grid.discovery;

import com.elasticgrid.model.Grid;
import com.elasticgrid.model.Node;
import java.util.EventObject;

/**
 * Event generated when a grid topology changed.
 */
public class GridChangedEvent<N extends Node> extends EventObject {
    private Grid<N> grid;

    /**
     * Constructs a {@link GridChangedEvent} Event.
     *
     * @param source The object on which the Event initially occurred.
     * @param grid the grid which changed
     * @throws IllegalArgumentException if source is null.
     */
    public GridChangedEvent(Object source, Grid<N> grid) {
        super(source);
        this.grid = grid;
    }

    public Grid<N> getGrid() {
        return grid;
    }
}
