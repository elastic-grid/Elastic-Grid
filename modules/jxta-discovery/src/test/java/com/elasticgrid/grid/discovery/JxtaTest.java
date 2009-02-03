/**
 * Elastic Grid
 * Copyright (C) 2008-2009 Elastic Grid, LLC.
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
