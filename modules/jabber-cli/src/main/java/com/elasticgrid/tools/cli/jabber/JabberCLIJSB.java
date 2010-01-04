/**
 * Elastic Grid
 * Copyright (C) 2008-2010 Elastic Grid, LLC.
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
package com.elasticgrid.tools.cli.jabber;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.NotFilter;
import org.jivesoftware.smack.filter.FromContainsFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.rioproject.core.jsb.ServiceBeanContext;
import org.rioproject.event.DynamicEventConsumer;
import org.rioproject.event.RemoteServiceEvent;
import org.rioproject.event.RemoteServiceEventListener;
import org.rioproject.jsb.ServiceBeanAdapter;
import org.rioproject.sla.SLAThresholdEvent;
import org.rioproject.tools.cli.OptionHandler;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.elasticgrid.tools.cli.CLI;

/**
 * @author Jerome Bernard
 */
public class JabberCLIJSB extends ServiceBeanAdapter implements JabberCLI, RemoteServiceEventListener {
//    private Chat chat;
    private XMPPConnection jabberConnection;
    private String jabberUser;
    private String jabberDomain;
    private String jabberPassword;
    private String egAdministrator;
    private String jabberServer;
    private int jabberPort = 5222;
    private DynamicEventConsumer consumer;
    private org.rioproject.tools.cli.CLI cli;
    private Logger logger = Logger.getLogger(getClass().getName());

    public void initialize(ServiceBeanContext context) throws Exception {
        super.initialize(context);
        jabberUser = (String) context.getConfiguration().getEntry(
            "com.elasticgrid.tools.cli.jabber",
            "jabberUser", String.class
        );
        jabberDomain = (String) context.getConfiguration().getEntry(
            "com.elasticgrid.tools.cli.jabber",
            "jabberDomain", String.class
        );
        jabberPassword = (String) context.getConfiguration().getEntry(
            "com.elasticgrid.tools.cli.jabber",
            "jabberPassword", String.class
        );
        egAdministrator = (String) context.getConfiguration().getEntry(
            "com.elasticgrid.tools.cli.jabber",
            "egAdministrator", String.class
        );
        jabberServer = (String) context.getConfiguration().getEntry(
            "com.elasticgrid.tools.cli.jabber",
            "jabberServer", String.class
        );
        jabberPort = Integer.parseInt((String) context.getConfiguration().getEntry(
            "com.elasticgrid.tools.cli.jabber",
            "jabberPort", String.class
        ));
        try {
            CLI.getClusterManager();
            cli = CLI.getInstance();
            logger.info("CLI is of class " + cli.getClass().getName());
            Runnable init = new Runnable() {
                public void run() {
                    try {
                        CLI.initCLI(new String[] {});
                    } catch (Throwable t) {
                        logger.log(Level.SEVERE, "Could not initialize Elastic Grid CLI", t);
                    }
                }
            };
            Thread initThread = new Thread(init, "Elastic Grid CLI Initialization");
            initThread.start();
            initializeJabberConnection();
            consumer =
                new DynamicEventConsumer(SLAThresholdEvent.getEventDescriptor(),
                                         this,
                                         context.getDiscoveryManagement());
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Could not initialize Elastic Grid CLI", t);
            throw new Exception("Can't initialize Elastic Grid CLI", t);
        }
    }

    private void initializeJabberConnection() throws XMPPException {
        logger.info(String.format("Initialization of Jabber connection as %s@%s on server %s:%d", jabberUser, jabberDomain, jabberServer, jabberPort));
        ConnectionConfiguration connectionConfiguration =
            new ConnectionConfiguration(jabberServer, jabberPort, jabberDomain);
        connectionConfiguration.setDebuggerEnabled(false);
        jabberConnection = new XMPPConnection(connectionConfiguration);
        logger.info("Connecting to server...");
        jabberConnection.connect();
        if (!jabberConnection.isConnected())
            throw new XMPPException("Can't connect to server " + jabberServer + " on port " + jabberPort);
        logger.info("Logging in....");
        jabberConnection.login(jabberUser, jabberPassword);
        Presence presence = new Presence(Presence.Type.available);
        jabberConnection.sendPacket(presence);
        logger.info("Logged in and in available status");

        // accept all buddies requesting to be added in the administrator roaster
//        Roster roster = jabberConnection.getRoster();
//        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);

        // create a chat with Elastic Grid Administrator
//        ChatManager chatManager = jabberConnection.getChatManager();
//        chatThreadID = "Elastic Grid Administration " + System.currentTimeMillis();
//        chat = chatManager.createChat(egAdministrator, chatThreadID, new CLIPacketListener());
        Message welcomeMessage = new Message(egAdministrator);
        welcomeMessage.setBody("You are administering an Elastic Grid Platform. With great power comes great responsability!");
        jabberConnection.sendPacket(welcomeMessage);

        // register for all incoming commands -- we're listening to all messages except those from us :-)
        PacketFilter filter = new AndFilter(
            new PacketTypeFilter(Message.class),
            new NotFilter(new FromContainsFilter(jabberUser + "@" + jabberDomain))
        );
        PacketListener cliListener = new CLIPacketListener();
        jabberConnection.addPacketListener(cliListener, filter);
    }

    public void destroy() {
        if (consumer != null) {
            consumer.deregister(this);
            consumer.terminate();
        }
        if (jabberConnection != null) {
            Presence presence = new Presence(Presence.Type.unavailable);
            jabberConnection.sendPacket(presence);
            jabberConnection.disconnect();
        }
        super.destroy();
    }

    public String executeCommand(String command) throws RemoteException {
        logger.info("Processing command '" + command + "'");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(out);

        // execute the command
        if (command != null && !"".equals(command)) {
            String option = command;
            StringTokenizer tok = new StringTokenizer(option);
            if (tok.countTokens() > 0)
                option = tok.nextToken();

            if (!cli.validCommand(option)) {
                output.println("Invalid command");
                output.println(cli.getInteractiveUsage());
            } else {
                OptionHandler handler = cli.getOptionHandler(option);
                if (handler != null) {
                    String response = handler.process(command,
                            null,
                            output);
                    if (response.length() > 0)
                        output.println(response);
                }
            }
        }

        return out.toString();
    }

    /**
     * Keeps track of SLA events.
     *
     * @param event the SLA event
     * @inheritDoc
     */
    public void notify(RemoteServiceEvent event) {
        if (event instanceof SLAThresholdEvent) {
            SLAThresholdEvent slaEvent = (SLAThresholdEvent) event;
            String body = slaEvent.getServiceElement().getName() + "." +
                             slaEvent.getServiceElement()
                                 .getOperationalStringName()+
                                 " SLA ["+slaEvent.getSLA().getIdentifier()+"] "+
                                 (slaEvent.getType()==SLAThresholdEvent.BREACHED
                                  ? "BREACHED":"CLEARED")+" " +
                                 "low="+slaEvent.getSLA().getCurrentLowThreshold()+", "+
                                 "high="+slaEvent.getSLA().getCurrentHighThreshold();
            logger.finest("Going to send message: " + body);
            try {
                if (jabberConnection == null ||
                    !jabberConnection.isConnected()) {
                    logger.log(Level.WARNING,
                               "Not connected with the Jabber server. Trying to create one.");
                    initializeJabberConnection();
                }
                Message message = new Message(egAdministrator);
                message.setBody(body);
                jabberConnection.sendPacket(message);
            } catch (XMPPException e) {
                logger.log(Level.WARNING, "Could not send SLA event", e);
            }
        } else {
            logger.log(Level.WARNING, "Unwanted event received: " + event);
        }
    }

    private class CLIPacketListener implements PacketListener, MessageListener {
        public void processPacket(Packet packet) {
            if (packet instanceof Message) {
                Message message = (Message) packet;
                logger.info("Message from: " + message.getFrom());
                logger.info("Message body: " + message.getBody());
                processMessage(message);
            } else {
                logger.log(Level.WARNING, "Can't process XMPP packet " + packet);
            }
        }

        private void processMessage(Message message) {
            PacketExtension extension = message.getExtension("x", "jabber:x:delay");
            // skip the message is it is resent from an earlier conversation
            if (extension != null && extension instanceof DelayInformation) {
                logger.info("Skipping command '" + message.getBody() + "'");
                return;
            }
            try {
                String body = executeCommand(message.getBody());
                Message reply = new Message(egAdministrator);
                reply.setBody(body);
                jabberConnection.sendPacket(reply);
            } catch (Exception e) {
                throw new UnsupportedOperationException("Can't process command " + message.getBody(), e);
            }
        }

        public void processMessage(Chat chat, Message message) {
            processMessage(message);
        }
    }

}
