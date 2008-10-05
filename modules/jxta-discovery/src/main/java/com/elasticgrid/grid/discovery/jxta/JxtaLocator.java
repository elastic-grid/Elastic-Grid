package com.elasticgrid.grid.discovery.jxta;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.DiscoveryResponseMsg;
import java.util.Enumeration;

/**
 * {@link com.elasticgrid.grid.discovery.GridLocator} implementation based on Jxta discovery mechanism.
 */
public class JxtaLocator implements DiscoveryListener {
    private transient NetworkManager networkManager;
    private transient DiscoveryService discoveryService;
    private static final long WAIT_TIMEOUT = 60 * 1000L; // one minute

    public void start() {
        discoveryService.addDiscoveryListener(this);
        discoveryService.getRemoteAdvertisements(
                null,                   // no specific peer (propagate)
                DiscoveryService.ADV,   // ADV type
                null,                   // attribute = any
                null,                   // value = any
                1,                      // one advertisement response is all we are looking for
                null                    // no query specific listener; we are using a global listener
        );
        while (true) {
            // wait a bit before sending a discoveryService message
            try {
                System.out.println("Sleeping for: " + WAIT_TIMEOUT);
                Thread.sleep(WAIT_TIMEOUT);
            } catch (Exception e) {
                // ignore
            }
            System.out.println("Sending a discovery message");
            discoveryService.getRemoteAdvertisements(
                    null,                   // no specific peer (propagate)
                    DiscoveryService.ADV,   // ADV type
                    "Name",                 // attribute = Name
                    "Elastic Grid",         // value = Elastic Grid
                    1,                      // one advertisement response is all we are looking for
                    null                    // no query specific listener; we are using a global listener
            );

        }
    }

    public void discoveryEvent(DiscoveryEvent event) {
        DiscoveryResponseMsg response = event.getResponse();
        System.out.println("[ Got a discovery response [" +
                response.getResponseCount() + " elements] from peer: " + event.getSource() + " ]");
        Advertisement adv;
        Enumeration en = response.getAdvertisements();
        if (en != null) {
            while (en.hasMoreElements()) {
                adv = (Advertisement) en.nextElement();
                System.out.println(adv);
            }
        }
    }

    public void stop() {
        networkManager.stopNetwork();
    }

    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    public void setDiscoveryService(DiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }
}
