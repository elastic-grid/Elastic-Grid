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
import org.apache.http.HttpException;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

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
     */
    public XMLCloudServers(String username, String apiKey) throws RackspaceException, IOException {
        super(username, apiKey);
    }

    public List<Server> getServers() throws CloudServersException {
        HttpGet request = new HttpGet(getServerManagementURL() + "/servers");
        Servers response = makeRequestInt(request, Servers.class);
        List<Server> servers = new ArrayList<Server>(response.getServers().size());
        for (com.rackspace.cloudservers.jibx.Server server : response.getServers())
            servers.add(new Server(server));
        return servers;
    }

    public List<Server> getServersWithDetails() throws CloudServersException {
        HttpGet request = new HttpGet(getServerManagementURL() + "/servers/details");
        Servers response = makeRequestInt(request, Servers.class);
        List<Server> servers = new ArrayList<Server>(response.getServers().size());
        for (com.rackspace.cloudservers.jibx.Server server : response.getServers())
            servers.add(new Server(server));
        return servers;
    }

    public Server getServerDetails(int serverID) throws CloudServersException {
        if (serverID == 0)
            throw new IllegalArgumentException("Invalid serverID " + serverID);
        HttpGet request = new HttpGet(getServerManagementURL() + "/servers/" + serverID);
        com.rackspace.cloudservers.jibx.Server response = makeRequestInt(request, com.rackspace.cloudservers.jibx.Server.class);
        return new Server(response);
    }

    public void updateServerName(int serverID, String name) throws CloudServersException {
        updateServerNameAndPassword(serverID, name, null);
    }

    public void updateServerPassword(int serverID, String password) throws CloudServersException {
        updateServerNameAndPassword(serverID, null, password);
    }

    public void updateServerNameAndPassword(final int serverID, final String name, final String password) throws CloudServersException {
        if (serverID == 0)
            throw new IllegalArgumentException("Invalid serverID " + serverID);
        HttpPut request = new HttpPut(getServerManagementURL() + "/servers/" + serverID);
        com.rackspace.cloudservers.jibx.Server server = new com.rackspace.cloudservers.jibx.Server();
        server.setId(serverID);
        if (name != null)
            server.setName(name);
        if (password != null)
            server.setAdminPass(password);
        makeEntityRequestInt(request, server);
    }

    public void createServer(String serverName, String imageID, String flavorID) {
        createServer(serverName, imageID, flavorID, null);
    }

    public void createServer(String serverName, String imageID, String flavorID, Map<String, String> metadata) {
        //To change body of implemented methods use File | Settings | File Templates.
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
}
