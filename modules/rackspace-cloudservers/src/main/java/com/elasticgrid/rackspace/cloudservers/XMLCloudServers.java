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
package com.elasticgrid.rackspace.cloudservers;

import com.elasticgrid.rackspace.common.RackspaceConnection;
import com.elasticgrid.rackspace.common.RackspaceException;
import com.rackspace.cloudservers.jibx.RateLimitUnit;
import com.rackspace.cloudservers.jibx.Servers;
import com.rackspace.cloudservers.jibx.Metadata;
import com.rackspace.cloudservers.jibx.MetadataItem;
import com.rackspace.cloudservers.jibx.Public;
import com.rackspace.cloudservers.jibx.Private;
import com.rackspace.cloudservers.jibx.Reboot;
import com.rackspace.cloudservers.jibx.Rebuild;
import com.rackspace.cloudservers.jibx.Resize;
import com.rackspace.cloudservers.jibx.ConfirmResize;
import com.rackspace.cloudservers.jibx.RevertResize;
import com.rackspace.cloudservers.jibx.Flavors;
import org.apache.http.HttpException;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Implementation based on the XML documents.
 *
 * @author Jerome Bernard
 */
public class XMLCloudServers extends RackspaceConnection implements CloudServers {
    private static final Logger logger = Logger.getLogger(XMLCloudServers.class.getName());

    /**
     * Initializes the Rackspace Cloud Servers connection with the Rackspace login information.
     *
     * @param username the Rackspace username
     * @param apiKey   the Rackspace API key
     * @throws RackspaceException if the credentials are invalid
     * @throws IOException        if there is a network issue
     * @see #authenticate()
     */
    public XMLCloudServers(String username, String apiKey) throws RackspaceException, IOException {
        super(username, apiKey);
    }

    public List<Server> getServers() throws CloudServersException {
        logger.info("Retrieving servers information...");
        HttpGet request = new HttpGet(getServerManagementURL() + "/servers");
        Servers response = makeRequestInt(request, Servers.class);
        List<Server> servers = new ArrayList<Server>(response.getServers().size());
        for (com.rackspace.cloudservers.jibx.Server server : response.getServers())
            servers.add(new Server(server));
        return servers;
    }

    public List<Server> getServersWithDetails() throws CloudServersException {
        logger.info("Retrieving detailed servers information...");
        HttpGet request = new HttpGet(getServerManagementURL() + "/servers/details");
        Servers response = makeRequestInt(request, Servers.class);
        List<Server> servers = new ArrayList<Server>(response.getServers().size());
        for (com.rackspace.cloudservers.jibx.Server server : response.getServers())
            servers.add(new Server(server));
        return servers;
    }

    public Server getServerDetails(int serverID) throws CloudServersException {
        logger.log(Level.INFO, "Retrieving detailed information for server {0}...", serverID);
        validateServerID(serverID);
        HttpGet request = new HttpGet(getServerManagementURL() + "/servers/" + serverID);
        com.rackspace.cloudservers.jibx.Server response = makeRequestInt(request, com.rackspace.cloudservers.jibx.Server.class);
        return new Server(response);
    }

    public Addresses getServerAddresses(int serverID) throws CloudServersException {
        logger.log(Level.INFO, "Retrieving all IP addresses of server {0}...", serverID);
        validateServerID(serverID);
        HttpGet request = new HttpGet(getServerManagementURL() + "/servers/" + serverID + "/ips");
        com.rackspace.cloudservers.jibx.Addresses response = makeRequestInt(request, com.rackspace.cloudservers.jibx.Addresses.class);
        try {
            return new Addresses(response);
        } catch (UnknownHostException e) {
            throw new CloudServersException("Can't validate server addresses", e);
        }
    }

    public List<InetAddress> getServerPublicAddresses(int serverID) throws CloudServersException {
        logger.log(Level.INFO, "Retrieving all public IP addresses of server {0}...", serverID);
        validateServerID(serverID);
        HttpGet request = new HttpGet(getServerManagementURL() + "/servers/" + serverID + "/ips/public");
        Public response = makeRequestInt(request, Public.class);
        try {
            List<InetAddress> addresses = new ArrayList<InetAddress>(response.getAddressLists().size());
            for (com.rackspace.cloudservers.jibx.Address address : response.getAddressLists()) {
                addresses.add(InetAddress.getByName(address.getAddr()));
            }
            return addresses;
        } catch (UnknownHostException e) {
            throw new CloudServersException("Can't validate server addresses", e);
        }
    }

    public List<InetAddress> getServerPrivateAddresses(int serverID) throws CloudServersException {
        logger.log(Level.INFO, "Retrieving all private IP addresses of server {0}...", serverID);
        validateServerID(serverID);
        HttpGet request = new HttpGet(getServerManagementURL() + "/servers/" + serverID + "/ips/private");
        Private response = makeRequestInt(request, Private.class);
        try {
            List<InetAddress> addresses = new ArrayList<InetAddress>(response.getAddressLists().size());
            for (com.rackspace.cloudservers.jibx.Address address : response.getAddressLists()) {
                addresses.add(InetAddress.getByName(address.getAddr()));
            }
            return addresses;
        } catch (UnknownHostException e) {
            throw new CloudServersException("Can't validate server addresses", e);
        }
    }

    public void shareAddress(int serverID, InetAddress address) throws CloudServersException {
        logger.log(Level.INFO, "Sharing IP address {0} with server {1}...", new Object[] { address, serverID });
        validateServerID(serverID);
        if (address == null)
            throw new IllegalArgumentException("Invalid IP address");
        HttpPut request = new HttpPut(getServerManagementURL() + "/servers/" + serverID
                + "/ips/public/" + address.getHostAddress());
        makeRequestInt(request);
    }

    public void unshareAddress(int serverID, InetAddress address) throws CloudServersException {
        logger.log(Level.INFO, "Unsharing IP address {0} with server {1}...", new Object[] { address, serverID });
        validateServerID(serverID);
        if (address == null)
            throw new IllegalArgumentException("Invalid IP address");
        HttpDelete request = new HttpDelete(getServerManagementURL() + "/servers/" + serverID
                + "/ips/public/" + address.getHostAddress());
        makeRequestInt(request);
    }

    public Server createServer(String name, int imageID, int flavorID) throws CloudServersException {
        return createServer(name, imageID, flavorID, null);
    }

    public Server createServer(String name, int imageID, int flavorID, Map<String, String> metadata) throws CloudServersException {
        logger.log(Level.INFO, "Creating server {0} from image {1} running on flavor {2}...",
                new Object[] { name, imageID, flavorID });
        if (name == null)
            throw new IllegalArgumentException("Server name has to be specified!");
        if (imageID == 0)
            throw new IllegalArgumentException("Image ID has to be specified!");
        if (flavorID == 0)
            throw new IllegalArgumentException("Flavor ID has to be specified!");
        HttpPost request = new HttpPost(getServerManagementURL() + "/servers");
        com.rackspace.cloudservers.jibx.Server server = new com.rackspace.cloudservers.jibx.Server();
        server.setName(name);
        server.setImageId(imageID);
        server.setFlavorId(flavorID);
        if (metadata != null && !metadata.isEmpty()) {
            Metadata rawMetadata = new Metadata();
            List<MetadataItem> metadataItems = rawMetadata.getMetadatas();
            for (Map.Entry<String, String> entry : metadata.entrySet()) {
                MetadataItem item = new MetadataItem();
                item.setKey(entry.getKey());
                item.setString(entry.getValue());
                metadataItems.add(item);
            }
            server.setMetadata(rawMetadata);
        }
        com.rackspace.cloudservers.jibx.Server created = makeEntityRequestInt(request, server, com.rackspace.cloudservers.jibx.Server.class);
        return new Server(created);
    }

    public void rebootServer(int serverID) throws CloudServersException {
        rebootServer(serverID, RebootType.SOFT);
    }

    public void rebootServer(int serverID, RebootType type) throws CloudServersException {
        logger.log(Level.INFO, "Rebooting server {0} via {1} reboot...", new Object[] { serverID, type.name() });
        validateServerID(serverID);
        HttpPost request = new HttpPost(getServerManagementURL() + "/servers/" + serverID + "/action");
        Reboot reboot = new Reboot();
        reboot.setType(com.rackspace.cloudservers.jibx.RebootType.valueOf(type.name()));
        makeEntityRequestInt(request, reboot);
    }

    public void rebuildServer(int serverID) throws CloudServersException {
        logger.log(Level.INFO, "Rebuilding server {0}...", serverID);
        validateServerID(serverID);
        HttpPost request = new HttpPost(getServerManagementURL() + "/servers/" + serverID + "/action");
        makeEntityRequestInt(request, new Rebuild());
    }

    public void rebuildServer(int serverID, int imageID) throws CloudServersException {
        logger.log(Level.INFO, "Rebuilding server {0} from image {1}...", new Object[] { serverID, imageID });
        validateServerID(serverID);
        HttpPost request = new HttpPost(getServerManagementURL() + "/servers/" + serverID + "/action");
        Rebuild rebuild = new Rebuild();
        rebuild.setImageId(imageID);
        makeEntityRequestInt(request, rebuild);
    }

    public void resizeServer(int serverID, int flavorID) throws CloudServersException {
        logger.log(Level.INFO, "Resizing server {0} to run on flavor {1}...", new Object[] { serverID, flavorID });
        validateServerID(serverID);
        HttpPost request = new HttpPost(getServerManagementURL() + "/servers/" + serverID + "/action");
        Resize resize = new Resize();
        resize.setFlavorId(flavorID);
        makeEntityRequestInt(request, resize);
    }

    public void confirmResize(int serverID) throws CloudServersException {
        logger.log(Level.INFO, "Confirming resize of server {0}...", serverID);
        validateServerID(serverID);
        HttpPost request = new HttpPost(getServerManagementURL() + "/servers/" + serverID + "/action");
        makeEntityRequestInt(request, new ConfirmResize());
    }

    public void revertResize(int serverID) throws CloudServersException {
        logger.log(Level.INFO, "Cancelling resize of server {0}...", serverID);
        validateServerID(serverID);
        HttpPost request = new HttpPost(getServerManagementURL() + "/servers/" + serverID + "/action");
        makeEntityRequestInt(request, new RevertResize());
    }

    public void updateServerName(int serverID, String name) throws CloudServersException {
        updateServerNameAndPassword(serverID, name, null);
    }

    public void updateServerPassword(int serverID, String password) throws CloudServersException {
        updateServerNameAndPassword(serverID, null, password);
    }

    public void updateServerNameAndPassword(final int serverID, final String name, final String password) throws CloudServersException {
        validateServerID(serverID);
        HttpPut request = new HttpPut(getServerManagementURL() + "/servers/" + serverID);
        com.rackspace.cloudservers.jibx.Server server = new com.rackspace.cloudservers.jibx.Server();
        server.setId(serverID);
        if (name != null)
            server.setName(name);
        if (password != null)
            server.setAdminPass(password);
        makeEntityRequestInt(request, server);
    }

    public void deleteServer(int serverID) throws CloudServersException {
        logger.log(Level.INFO, "Deleting server {0}...", serverID);
        validateServerID(serverID);
        HttpDelete request = new HttpDelete(getServerManagementURL() + "/servers/" + serverID);
        makeRequestInt(request);
    }

    public Limits getLimits() throws CloudServersException {
        HttpGet request = new HttpGet(getServerManagementURL() + "/limits");
        com.rackspace.cloudservers.jibx.Limits response = makeRequestInt(request, com.rackspace.cloudservers.jibx.Limits.class);
        List<RateLimit> rateLimits = new ArrayList<RateLimit>(response.getRate().getRateLimits().size());
        for (com.rackspace.cloudservers.jibx.RateLimit limit : response.getRate().getRateLimits())
            rateLimits.add(new RateLimit(
                    HTTPVerb.valueOf(limit.getVerb().name()),
                    limit.getURI(),
                    limit.getRegex(),
                    limit.getValue(),
                    limit.getRemaining(),
                    translateRateLimitUnit(limit.getUnit()),
                    limit.getResetTime()
            ));
        List<AbsoluteLimit> absoluteLimits = new ArrayList<AbsoluteLimit>(response.getAbsolute().getAbsoluteLimits().size());
        for (com.rackspace.cloudservers.jibx.AbsoluteLimit limit : response.getAbsolute().getAbsoluteLimits())
            absoluteLimits.add(new AbsoluteLimit(limit.getName(), limit.getValue()));
        return new Limits(rateLimits, absoluteLimits);
    }

    public List<Flavor> getFlavors() throws CloudServersException {
        logger.info("Retrieving flavors information...");
        HttpGet request = new HttpGet(getServerManagementURL() + "/flavors");
        Flavors response = makeRequestInt(request, Flavors.class);
        List<Flavor> flavors = new ArrayList<Flavor>(response.getFlavors().size());
        for (com.rackspace.cloudservers.jibx.Flavor flavor : response.getFlavors())
            flavors.add(new Flavor(flavor.getId(), flavor.getName(), flavor.getRam(), flavor.getDisk()));
        return flavors;
    }

    public List<Flavor> getFlavorsWithDetails() throws CloudServersException {
        logger.info("Retrieving detailed flavors information...");
        HttpGet request = new HttpGet(getServerManagementURL() + "/flavors/details");
        Flavors response = makeRequestInt(request, Flavors.class);
        List<Flavor> flavors = new ArrayList<Flavor>(response.getFlavors().size());
        for (com.rackspace.cloudservers.jibx.Flavor flavor : response.getFlavors())
            flavors.add(new Flavor(flavor.getId(), flavor.getName(), flavor.getRam(), flavor.getDisk()));
        return flavors;
    }

    public Flavor getFlavorDetails(int flavorID) throws CloudServersException {
        logger.log(Level.INFO, "Retrieving detailed information for flavor {0}...", flavorID);
        validateFlavorID(flavorID);
        HttpGet request = new HttpGet(getServerManagementURL() + "/flavors/" + flavorID);
        com.rackspace.cloudservers.jibx.Flavor response = makeRequestInt(request, com.rackspace.cloudservers.jibx.Flavor.class);
        return new Flavor(response.getId(), response.getName(), response.getRam(), response.getDisk());
    }

    private TimeUnit translateRateLimitUnit(RateLimitUnit unit) {
        switch (unit) {
            case MINUTE:
                return TimeUnit.MINUTES;
            case HOUR:
                return TimeUnit.HOURS;
            case DAY:
                return TimeUnit.DAYS;
            default:
                throw new IllegalStateException("Unexpected enum value: " + unit);
        }
    }

    protected void makeEntityRequestInt(HttpEntityEnclosingRequestBase request, final Object entity) throws CloudServersException {
        makeEntityRequestInt(request, entity, Void.class);
    }

    protected <T> T makeEntityRequestInt(HttpEntityEnclosingRequestBase request, final Object entity, Class<T> respType) throws CloudServersException {
        request.setEntity(new EntityTemplate(new ContentProducer() {
            public void writeTo(OutputStream output) throws IOException {
                try {
                    IBindingFactory bindingFactory = BindingDirectory.getFactory(entity.getClass());
                    final IMarshallingContext marshallingCxt = bindingFactory.createMarshallingContext();
                    marshallingCxt.marshalDocument(entity, "UTF-8", true, output);
                } catch (JiBXException e) {
                    IOException ioe = new IOException("Can't marshal server details");
                    ioe.initCause(e);
                    e.printStackTrace();
                    throw ioe;
                }
            }
        }));
        return makeRequestInt(request, respType);
    }

    protected void makeRequestInt(HttpRequestBase request) throws CloudServersException {
        makeRequestInt(request, Void.class);
    }

    protected <T> T makeRequestInt(HttpRequestBase request, Class<T> respType) throws CloudServersException {
        try {
            return makeRequest(request, respType);
        } catch (RackspaceException e) {
            throw new CloudServersException(e);
        } catch (JiBXException e) {
            throw new CloudServersException("Problem parsing returned message.", e);
        } catch (MalformedURLException e) {
            throw new CloudServersException(e.getMessage(), e);
        } catch (IOException e) {
            throw new CloudServersException(e.getMessage(), e);
        } catch (HttpException e) {
            throw new CloudServersException(e.getMessage(), e);
        }
    }

    private void validateServerID(int serverID) throws IllegalArgumentException {
        if (serverID == 0)
            throw new IllegalArgumentException("Invalid serverID " + serverID);
    }

    private void validateFlavorID(int flavorID) throws IllegalArgumentException {
        if (flavorID == 0)
            throw new IllegalArgumentException("Invalid flavorID " + flavorID);
    }
}
