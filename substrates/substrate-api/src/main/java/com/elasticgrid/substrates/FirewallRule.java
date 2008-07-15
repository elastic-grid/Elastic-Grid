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
