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

import com.rackspace.cloudservers.jibx.Public;
import com.rackspace.cloudservers.jibx.Private;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.ArrayList;

public class Addresses implements Serializable {
    private final List<InetAddress> publicAddresses;
    private final List<InetAddress> privateAddresses;

    public Addresses(List<InetAddress> publicAddresses, List<InetAddress> privateAddresses) {
        this.publicAddresses = publicAddresses;
        this.privateAddresses = privateAddresses;
    }

    public Addresses(com.rackspace.cloudservers.jibx.Addresses addresses) throws UnknownHostException {
        // populate public addresses list
        Public publicAddresses = addresses.getPublic();
        this.publicAddresses = new ArrayList<InetAddress>(publicAddresses.getAddressLists().size());
        for (com.rackspace.cloudservers.jibx.Address address : publicAddresses.getAddressLists()) {
            this.publicAddresses.add(InetAddress.getByName(address.getAddr()));
        }
        // populate private addresses list
        Private privateAddresses = addresses.getPrivate();
        this.privateAddresses = new ArrayList<InetAddress>(privateAddresses.getAddressLists().size());
        for (com.rackspace.cloudservers.jibx.Address address : privateAddresses.getAddressLists()) {
            this.privateAddresses.add(InetAddress.getByName(address.getAddr()));
        }
    }

    public List<InetAddress> getPublicAddresses() {
        return publicAddresses;
    }

    public List<InetAddress> getPrivateAddresses() {
        return privateAddresses;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Addresses");
        sb.append("{publicAddresses=").append(publicAddresses);
        sb.append(", privateAddresses=").append(privateAddresses);
        sb.append('}');
        return sb.toString();
    }
}