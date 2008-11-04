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

package com.elasticgrid.platforms.lan.discovery;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import com.elasticgrid.model.ClusterException;
import java.rmi.RMISecurityManager;
import java.security.Permission;

public class LANClusterLocatorTest {
    private LANClusterLocator locator;

    @Test
    public void testLocator() throws ClusterException {
//        locator.findClusters();
    }

    @BeforeClass
    public void setupClusterLocator() {
        System.setSecurityManager(new RMISecurityManager() {
            @Override
            public void checkPermission(Permission perm) {
                // do nothing -- allow everything
            }
        });
        locator = new JiniGroupsClusterLocator();
    }

}
