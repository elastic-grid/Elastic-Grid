package com.elasticgrid.rackspace.cloudservers;

import java.io.Serializable;
import java.util.Map;
import java.util.List;
import java.net.InetAddress;

/**
 * Rackspace Server.
 * @author Jerome Bernard
 */
public class Server implements Serializable {
    private final String id;
    private final String name;
    private final String imageID;
    private final String flavorID;
    private final Status status;
    private final Map<String, String> metadata;
    private final List<InetAddress> publicAddresses;
    private final List<InetAddress> privateAddresses;
    private final Personality personality;

    public Server(String id, String name, String imageID, String flavorID, Server.Status status, Map<String, String> metadata, List<java.net.InetAddress> publicAddresses, java.util.List<java.net.InetAddress> privateAddresses, Personality personality) {
        this.id = id;
        this.name = name;
        this.imageID = imageID;
        this.flavorID = flavorID;
        this.status = status;
        this.metadata = metadata;
        this.publicAddresses = publicAddresses;
        this.privateAddresses = privateAddresses;
        this.personality = personality;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageID() {
        return imageID;
    }

    public String getFlavorID() {
        return flavorID;
    }

    public com.elasticgrid.rackspace.cloudservers.Server.Status getStatus() {
        return status;
    }

    public java.util.Map<String, String> getMetadata() {
        return metadata;
    }

    public java.util.List<java.net.InetAddress> getPublicAddresses() {
        return publicAddresses;
    }

    public java.util.List<java.net.InetAddress> getPrivateAddresses() {
        return privateAddresses;
    }

    public enum Status {
        BUILD, REBUILD, SUSPENDED, QUEUE_RESIZE, PREP_RESIZE, VERIFY_RESIZE, PASSWORD, RESCUE, REBOOT,
        HARD_REBOOT, SHARE_IP, SHARE_IP_NO_CONFIG, DELETE_IP, UNKNOWN
    }

}
