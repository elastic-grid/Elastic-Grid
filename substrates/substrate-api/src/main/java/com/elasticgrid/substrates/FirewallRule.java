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
package com.elasticgrid.substrates;

import java.io.Serializable;

/**
 * Firewall rule.
 */
public class FirewallRule implements Serializable {
    private String service;
    private IpProtocol ipProtocol;
    private int fromPort = 0, toPort = 65535;
    private String cidrIp;

    public FirewallRule(String service, IpProtocol ipProtocol, int port, String cidrIp) {
        this(service, ipProtocol, port, port, cidrIp);
    }

    public FirewallRule(String service, IpProtocol ipProtocol, int fromPort, int toPort, String cidrIp) {
        this.service = service;
        this.ipProtocol = ipProtocol;
        this.fromPort = fromPort;
        this.toPort = toPort;
        this.cidrIp = cidrIp;
    }

    public String getService() {
        return service;
    }

    public IpProtocol getIpProtocol() {
        return ipProtocol;
    }

    public int getFromPort() {
        return fromPort;
    }

    public int getToPort() {
        return toPort;
    }

    public String getCidrIp() {
        return cidrIp;
    }

    public enum IpProtocol implements Serializable {
        TCP("tcp"), UDP("udp"), ICMP("icmp");

        private String value;

        IpProtocol(String value) {
            this.value = value;
        }
    }
}
