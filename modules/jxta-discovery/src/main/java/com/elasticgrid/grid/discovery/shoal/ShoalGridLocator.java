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

package com.elasticgrid.grid.discovery.shoal;

import com.elasticgrid.model.Node;
import com.elasticgrid.model.GridException;
import com.elasticgrid.model.GridNotFoundException;
import com.elasticgrid.model.GridMonitorNotFoundException;
import com.elasticgrid.grid.discovery.GridLocator;
import com.elasticgrid.grid.discovery.AbstractGridLocator;
import com.sun.enterprise.ee.cms.core.GroupManagementService;
import java.util.List;

/**
 * {@link GridLocator} implementation based on <a href="http://shoal.dev.java.net">Shoal</a>.
 */
public class ShoalGridLocator extends AbstractGridLocator<Node> {
    private GroupManagementService gms;

    public List<String> findGrids() throws GridException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<Node> findNodes(String gridName) throws GridNotFoundException, GridException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Node findMonitor(String gridName) throws GridMonitorNotFoundException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
