package com.elasticgrid.rackspace.cloudservers;

import com.elasticgrid.rackspace.cloudservers.CloudServers;
import com.elasticgrid.rackspace.cloudservers.Server;
import java.util.Map;
import java.util.List;

/**
 * Implementation based on the XML documents.
 * @author Jerome Bernard
 */
public class XMLCloudServers implements CloudServers {
    public List<Server> getServers() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<Server> getServersWithDetails() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void createServer(String serverName, String imageID, String flavorID) {
        createServer(serverName, imageID, flavorID, null);
    }

    public void createServer(String serverName, String imageID, String flavorID, Map<String, String> metadata) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
