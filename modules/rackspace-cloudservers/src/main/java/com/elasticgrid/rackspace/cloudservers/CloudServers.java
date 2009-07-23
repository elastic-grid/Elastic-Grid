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
    List<Server> getServers();

    /**
     * Retreive the list of servers (with details) associated with the Rackspace account.
     * @return the list of servers
     */
    List<Server> getServersWithDetails();

    /**
     * Provision a new server.
     * @param serverName the name of the server to create
     * @param imageID the image from which the server should be created
     * @param flavorID the kind of hardware to use
     */
    void createServer(String serverName, String imageID, String flavorID);

    /**
     * Provision a new server.
     * @param serverName the name of the server to create
     * @param imageID the image from which the server should be created
     * @param flavorID the kind of hardware to use
     * @param metadata the launch metadata
     */
    void createServer(String serverName, String imageID, String flavorID, Map<String, String> metadata);

}
