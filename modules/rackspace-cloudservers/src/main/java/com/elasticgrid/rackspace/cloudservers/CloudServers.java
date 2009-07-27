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

import java.util.List;
import java.util.Map;

/**
 * Rackspace Cloud Servers API.
 * @author Jerome Bernard
 */
public interface CloudServers {

    /**
     * Retreive the list of servers (only IDs and names) associated with the Rackspace account.
     * @return the list of servers
     */
    List<Server> getServers() throws CloudServersException;

    /**
     * Retreive the list of servers (with details) associated with the Rackspace account.
     * @return the list of servers
     */
    List<Server> getServersWithDetails() throws CloudServersException;

    /**
     * Provision a new server.
     * @param serverName the name of the server to create
     * @param imageID the image from which the server should be created
     * @param flavorID the kind of hardware to use
     */
    void createServer(String serverName, String imageID, String flavorID) throws CloudServersException;

    /**
     * Provision a new server.
     * @param serverName the name of the server to create
     * @param imageID the image from which the server should be created
     * @param flavorID the kind of hardware to use
     * @param metadata the launch metadata
     */
    void createServer(String serverName, String imageID, String flavorID, Map<String, String> metadata) throws CloudServersException;

}
