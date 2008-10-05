package com.elasticgrid.grid.discovery;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryService;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeService;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.exception.PeerGroupException;
import java.io.IOException;
import java.io.File;
import java.util.Enumeration;

public class FakeMonitor implements DiscoveryListener {
    private NetworkManager networkManager;
    private DiscoveryService discoveryService;

    public void start() throws IOException {
        long lifetime = 60 * 2 * 1000L;
        long expiration = 60 * 2 * 1000L;
        long waittime = 60 * 3 * 1000L;
        while (true) {
            PipeAdvertisement pipeAdv = getPipeAdvertisement();
            // publish the advertisement with a lifetime of 2 mintutes
            System.out.printf("Publishing the following advertisement with lifetime %d and expiration %d\n",
                                lifetime, expiration);
            System.out.println(pipeAdv.toString());
            discoveryService.addDiscoveryListener(this);
            discoveryService.publish(pipeAdv, lifetime, expiration);
            discoveryService.remotePublish(pipeAdv, expiration);
            try {
                System.out.println("Sleeping for: " + waittime);
                Thread.sleep(waittime);
            } catch (Exception e) {
                //ignored
            }
        }
    }

    /**
     * This method is called whenever a discovery response is received, which are
     * either in response to a query we sent, or a remote publish by another node
     *
     * @param ev the discovery event
     */
    public void discoveryEvent(DiscoveryEvent ev) {
        DiscoveryResponseMsg res = ev.getResponse();
        // let's get the responding peer's advertisement
        System.out.println(" [  Got a Discovery Response [" +
                res.getResponseCount() + " elements]  from peer : " + ev.getSource() + "  ]");
        Advertisement adv;
        Enumeration en = res.getAdvertisements();
        if (en != null) {
            while (en.hasMoreElements()) {
                adv = (Advertisement) en.nextElement();
                System.out.println(adv);
            }
        }
    }

    public static PipeAdvertisement getPipeAdvertisement() {
        PipeAdvertisement advertisement = (PipeAdvertisement)
                AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
        advertisement.setPipeID(IDFactory.newPipeID(PeerGroupID.defaultNetPeerGroupID));
        advertisement.setType(PipeService.UnicastType);
        advertisement.setName("Elastic Grid");
        return advertisement;
    }

    /**
     * Stops the platform
     */
    public void stop() {
        networkManager.stopNetwork();
    }

    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    public void setDiscoveryService(DiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    public static void main(String[] args) throws IOException, PeerGroupException {
        NetworkManager networkManager = new NetworkManager(NetworkManager.ConfigMode.ADHOC,
                "Elastic Grid Fake Monitor", new File(new File(".cache"), "elastic-grid-fake").toURI());
        networkManager.startNetwork();
        PeerGroup peerGroup = networkManager.getNetPeerGroup();
        DiscoveryService discoveryService = peerGroup.getDiscoveryService();

        FakeMonitor fakeMonitor = new FakeMonitor();
        fakeMonitor.setNetworkManager(networkManager);
        fakeMonitor.setDiscoveryService(discoveryService);
        fakeMonitor.start();
    }
}
