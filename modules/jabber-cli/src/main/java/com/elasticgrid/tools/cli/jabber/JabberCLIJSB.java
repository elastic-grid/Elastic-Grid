/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * Licensed under the GNU Lesser General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.elasticgrid.tools.cli.jabber;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromContainsFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.NotFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.rioproject.core.jsb.ServiceBeanContext;
import org.rioproject.event.DynamicEventConsumer;
import org.rioproject.event.RemoteServiceEvent;
import org.rioproject.event.RemoteServiceEventListener;
import org.rioproject.jsb.ServiceBeanAdapter;
import org.rioproject.qos.SLAThresholdEvent;
import org.rioproject.resources.util.RioVersion;
import org.rioproject.tools.cli.CLI;
import org.rioproject.tools.cli.OptionHandler;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.StringTokenizer;

/**
 * @author Jerome Bernard
 */
public class JabberCLIJSB extends ServiceBeanAdapter implements JabberCLI, RemoteServiceEventListener {
    protected MultiUserChat chat;
    protected XMPPConnection jabberConnection;
    private String jabberUser;
    private String jabberDomain;
    private String jabberPassword;
    private String jabberChatroom;
    private DynamicEventConsumer consumer;
    private CLI cli;
    protected Logger logger = Logger.getLogger(getClass().getName());

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
        jabberChatroom = (String) context.getConfiguration().getEntry(
                "com.elasticgrid.tools.cli.jabber",
                "jabberChatroom", String.class
        );
        try {
            logger.info("In init()");
            cli = CLI.getInstance();
            //CLI.initCLI(new String[] {});
            initializeJabberConnection();
            consumer = new DynamicEventConsumer(SLAThresholdEvent.getEventDescriptor(),
                                            this,
                                            context.getDiscoveryManagement());
        } catch (Throwable t) {
            logger.log(Level.WARNING, "Can't connect to Jabber server", t);
            t.printStackTrace();
        }
    }

    private void initializeJabberConnection() throws XMPPException {
        XMPPConnection.DEBUG_ENABLED = true;
        logger.info("Initialization of Jabber connection");
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(jabberDomain, 5223, jabberDomain);
        connectionConfiguration.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
        logger.info("Initiating Jabber connection");
        jabberConnection = new XMPPConnection(connectionConfiguration);
        logger.info("Logging in");
        jabberConnection.login(jabberUser, jabberPassword);

        // accept all buddies requesting to be added in the administrator roaster
        Roster roster = jabberConnection.getRoster();
        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);

        // try to create a jabberChatroom, otherwise try to join it
        chat = new MultiUserChat(jabberConnection, jabberChatroom);
        try {
            chat.create(jabberUser);
            chat.sendConfigurationForm(new Form(Form.TYPE_SUBMIT));
        } catch (Exception e) {
            chat.join(jabberUser);
        }
        chat.invite("jerome.bernard@kalixia.com", "Rio platform administration");
        chat.sendMessage("You are administering a Rio " + RioVersion.VERSION + " platform. With great power comes great responsability!");

        // register for all incoming commands -- we're listening to all messages except those from us :-)
        PacketFilter filter = new AndFilter(
                new MessageTypeFilter(Message.Type.chat),
                new NotFilter(new FromContainsFilter(jabberUser))
        );
        PacketListener cliListener = new CLIPacketListener();
        jabberConnection.addPacketListener(cliListener, filter);
    }

    public void destroy() {
        if(consumer != null) {
            consumer.deregister(this);
            consumer.terminate();
        }
        if (chat != null)
            chat.leave();
        if (jabberConnection != null)
            jabberConnection.disconnect();
        super.destroy();
    }

    public String executeCommand(String command) throws RemoteException {
        try {
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
                        String response = handler.process(command, null, output);
                        if (response.length() > 0)
                            output.println(response);
                    }
                }
            }

            String result = out.toString();
            chat.sendMessage(result);
            return result;
        } catch (XMPPException e) {
            logger.log(Level.SEVERE, "Can't execute command " + command, e);
            throw new RemoteException(e.getMessage());
        }
    }

    /**
     * Keeps track of SLA events.
     * @param event the SLA event
     * @inheritDoc
     */
    public void notify(RemoteServiceEvent event) {
        if (event instanceof SLAThresholdEvent) {
            SLAThresholdEvent slaEvent = (SLAThresholdEvent)event;
            String message = slaEvent.getServiceElement().getName()+"."+
                   slaEvent.getServiceElement().getOperationalStringName()+" "+
                   "SLA ["+slaEvent.getSLA().getIdentifier()+"] "+
                   (slaEvent.getType() == SLAThresholdEvent.BREACHED?"BREACHED":
                    "CLEARED")+" " +
                    "low="+slaEvent.getSLA().getCurrentLowThreshold()+", " +
                    "high="+slaEvent.getSLA().getCurrentHighThreshold();
            logger.finest("Going to send message: " + message);
            try {
                if (jabberConnection == null || !jabberConnection.isConnected()) {
                    logger.log(Level.WARNING, "Not connected with the Jabber server. Trying to create one.");
                    initializeJabberConnection();
                }
                chat.sendMessage(message);
            } catch (XMPPException e) {
                logger.log(Level.WARNING, "Could not send SLA event", e);
            }
        } else {
            logger.log(Level.WARNING, "Unwanted event received: " + event);
        }
    }

    private class CLIPacketListener implements PacketListener {
        public void processPacket(Packet packet) {
            if (packet instanceof Message) {
                Message message = (Message) packet;
                PacketExtension extension = message.getExtension("x", "jabber:x:delay");
                // skip the message is it is resent from an earlier conversation
                if (extension != null && extension instanceof DelayInformation) {
                    logger.info("Skipping command '" + message.getBody() + "'");
                    return;
                }
                try {
                    executeCommand(message.getBody());
                } catch (RemoteException e) {
                    throw new UnsupportedOperationException("Can't process command " + message.getBody(), e);
                }
            } else {
                logger.log(Level.WARNING, "Can't process XMPP packet " + packet);
            }
        }
    }

}
