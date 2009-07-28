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

import com.rackspace.cloudservers.jibx.Metadata;
import com.rackspace.cloudservers.jibx.MetadataItem;
import java.io.Serializable;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.net.InetAddress;

/**
 * Rackspace Server.
 * @author Jerome Bernard
 */
public class Server implements Serializable {
    private final Integer id;
    private final String name;
    private final String adminPass;
    private final Integer imageID;
    private final Integer flavorID;
    private final Status status;
    private final Map<String, String> metadata;
    private final List<InetAddress> publicAddresses;
    private final List<InetAddress> privateAddresses;
    private final Personality personality;

    public Server(Integer id, String name, String adminPass, Integer imageID, Integer flavorID, Server.Status status,
                  Map<String, String> metadata, List<InetAddress> publicAddresses, List<InetAddress> privateAddresses,
                  Personality personality) {
        this.id = id;
        this.name = name;
        this.adminPass = adminPass;
        this.imageID = imageID;
        this.flavorID = flavorID;
        this.status = status;
        this.metadata = metadata;
        this.publicAddresses = publicAddresses;
        this.privateAddresses = privateAddresses;
        this.personality = personality;
    }

    public Server(final com.rackspace.cloudservers.jibx.Server server) {
        this(
                server.getId(), server.getName(), server.getAdminPass(), server.getImageId(), server.getFlavorId(),
                Status.valueOf(server.getStatus().name()),
                metadataAsMap(server.getMetadata()),
                null, // TODO: public addresses
                null, // TODO: private addresse
                new Personality(server.getPersonality())
        );
    }

    private static Map<String, String> metadataAsMap(Metadata metadata) {
        Map<String, String> meta = new HashMap<String, String>();
        for (MetadataItem item : metadata.getMetadatas()) {
            meta.put(item.getKey(), item.getString());
        }
        return meta;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getImageID() {
        return imageID;
    }

    public Integer getFlavorID() {
        return flavorID;
    }

    public Status getStatus() {
        return status;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public List<java.net.InetAddress> getPublicAddresses() {
        return publicAddresses;
    }

    public List<java.net.InetAddress> getPrivateAddresses() {
        return privateAddresses;
    }

    public enum Status {
        ACTIVE, SUSPENDED, DELETED, QUEUE_RESIZE, PREP_RESIZE, RESIZE, VERIFY_RESIZE,
        QUEUE_MOVE, PREP_MOVE, MOVE, VERIFY_MOVE, RESCUE, ERROR, BUILD, RESTORING,
        PASSWORD, REBUILD, DELETE_IP, SHARE_IP_NO_CONFIG, SHARE_IP, REBOOT, HARD_REBOOT, UNKNOWN
    }

}
