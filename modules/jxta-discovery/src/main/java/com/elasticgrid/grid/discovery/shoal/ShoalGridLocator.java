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
