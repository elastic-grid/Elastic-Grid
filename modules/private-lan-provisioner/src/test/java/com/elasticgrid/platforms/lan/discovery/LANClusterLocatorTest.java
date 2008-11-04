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
