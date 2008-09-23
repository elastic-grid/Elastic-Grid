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

package com.elasticgrid.tools.ui;

import com.elasticgrid.model.Grid;
import javax.swing.*;
import java.util.List;
import java.util.Collections;

public class GridsListModel extends AbstractListModel {
    private List<Grid> grids = Collections.emptyList();

    public int getSize() {
        return grids.size();
    }

    public Object getElementAt(int index) {
        return grids.get(index);
    }

    public void setGrids(List<Grid> grids) {
        this.grids = grids;
        super.fireContentsChanged(this, 0, grids.size());
    }
}
