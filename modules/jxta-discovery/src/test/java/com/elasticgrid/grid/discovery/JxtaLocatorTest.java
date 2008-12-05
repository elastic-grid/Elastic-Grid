/**
 * Elastic Grid
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elasticgrid.grid.discovery;

import com.elasticgrid.grid.discovery.jxta.JxtaLocator;
import com.elasticgrid.model.GridException;
import net.jxta.discovery.DiscoveryService;
import net.jxta.exception.PeerGroupException;
import net.jxta.logging.Logging;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class JxtaLocatorTest {
    private JxtaLocator locator;
    private FakeMonitor fakeMonitor;
    private Thread fakeMonitorThread;

    @Test
    public void testLocator() {
        assert true;
    }

    @BeforeTest
    public void setupLocator() throws GridException, IOException, PeerGroupException {
        NetworkManager networkManager = new NetworkManager(NetworkManager.ConfigMode.ADHOC,
                "Elastic Grid Discovery Client", new File(new File(".cache"), "elastic-grid").toURI());
        networkManager.startNetwork();
        PeerGroup peerGroup = networkManager.getNetPeerGroup();
        DiscoveryService discoveryService = peerGroup.getDiscoveryService();

//        fakeMonitor = new FakeMonitor();
//        fakeMonitor.setNetworkManager(networkManager);
//        fakeMonitor.setDiscoveryService(discoveryService);
//        fakeMonitorThread = new Thread(new Runnable() {
//            public void run() {
//                try {
//                    fakeMonitor.start();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        fakeMonitorThread.start();

        locator = new JxtaLocator();
        locator.setNetworkManager(networkManager);
        locator.setDiscoveryService(discoveryService);
        locator.start();
    }

    @AfterTest
    public void tearDownLocator() {
        locator.stop();
//        fakeMonitor.stop();
//        fakeMonitorThread.interrupt();
    }

    static {
        System.setProperty(Logging.JXTA_LOGGING_PROPERTY, Level.SEVERE.toString());
    }

}