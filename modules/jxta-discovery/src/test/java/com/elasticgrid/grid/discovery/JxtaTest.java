package com.elasticgrid.grid.discovery;

import net.jxta.platform.NetworkManager;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.endpoint.Message;
import net.jxta.peergroup.PeerGroup;
import net.jxta.exception.PeerGroupException;
import net.jxta.logging.Logging;
import java.text.MessageFormat;
import java.io.IOException;
import java.io.File;
import java.util.logging.Level;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import javax.security.cert.CertificateException;
import javax.swing.*;

/**
 * Jxta tests
 */
public class JxtaTest {
    private NetworkManager networkManager;
    private static final String LOCAL_PEER_NAME = "My Local Peer";
    private static final String LOCAL_NETWORK_MANAGER_NAME = "My Local Network Manager";

    static {
        System.setProperty(Logging.JXTA_LOGGING_PROPERTY, Level.SEVERE.toString()); 
    }

    @Test
    public void testRendezVousConnection() throws IOException, PeerGroupException {
        System.out.println("Starting JXTA");
        PeerGroup peerGroup = networkManager.startNetwork();
        System.out.println("JXTA started");

        System.out.println("Peer Name: " + peerGroup.getPeerName());
        System.out.println("Peer Group Name: " + peerGroup.getPeerGroupName());
        System.out.println("Peer Group ID: " + peerGroup.getPeerID());
        System.out.println("Waiting for a rendez-vous connection for up to 25 seconds");
        boolean connected = networkManager.waitForRendezvousConnection(25000);
        assert connected;
        System.out.println("Stopping JXTA");
        networkManager.stopNetwork();
    }

    @BeforeTest
    public void setupJxtaDiscovery() throws IOException, CertificateException {
        NetworkConfigurator networkConfigurator;
        networkManager = new NetworkManager(NetworkManager.ConfigMode.RENDEZVOUS, LOCAL_NETWORK_MANAGER_NAME);
        System.out.println("Network Manager created");
        networkManager.setConfigPersistent(true);
        System.out.println("PeerID: " + networkManager.getPeerID().toString());

        System.out.println("Retrieving the netzork configurator");
        networkConfigurator = networkManager.getConfigurator();
        System.out.println("Network configurator retrieved");

        if (networkConfigurator.exists()) {
            System.out.println("Local configuration found");
            File localConfig = new File(networkConfigurator.getHome(), "PlatformConfig");
            System.out.println("Loading found configuration");
            networkConfigurator.load(localConfig.toURI());
        } else {
            System.out.println("No local configuration found");
            networkConfigurator.setName(LOCAL_PEER_NAME);
            networkConfigurator.setPrincipal(getPrincipal());
            networkConfigurator.setPassword(getPassword());
            System.out.println("Principal: " + networkConfigurator.getPrincipal());
            System.out.println("Password : " + networkConfigurator.getPassword());
            System.out.println("Saving new configuration");
            networkConfigurator.save();
            System.out.println("New configuration saved successfully");
        }
    }

    private String getPrincipal() {
        return (String) JOptionPane.showInputDialog(
                null, "Enter principal", "Principal", JOptionPane.QUESTION_MESSAGE,
                null, null, "");
    }

    private String getPassword() {
        return (String) JOptionPane.showInputDialog(
                null, "Enter password", "Password", JOptionPane.QUESTION_MESSAGE,
                null, null, ""); 
    }

}
