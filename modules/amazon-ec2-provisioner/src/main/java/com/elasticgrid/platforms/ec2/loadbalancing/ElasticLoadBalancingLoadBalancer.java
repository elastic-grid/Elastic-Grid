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
package com.elasticgrid.platforms.ec2.loadbalancing;

import com.elasticgrid.loadbalancing.spi.LoadBalancer;
import com.elasticgrid.loadbalancing.LoadBalancingException;
import com.elasticgrid.loadbalancing.Endpoint;
import com.elasticgrid.loadbalancing.LoadBalancingStrategy;
import com.xerox.amazonws.ec2.HealthCheck;
import com.xerox.amazonws.ec2.Listener;
import java.util.Calendar;
import java.util.List;
import java.rmi.RemoteException;

/**
 * Load Balancer built on top of Amazon Elastic LoadBalancing service.
 * @author Jerome Bernard
 */
public class ElasticLoadBalancingLoadBalancer implements LoadBalancer {
    private final com.xerox.amazonws.ec2.LoadBalancer lb;

    public ElasticLoadBalancingLoadBalancer(com.xerox.amazonws.ec2.LoadBalancer lb) {
        this.lb = lb;
    }

    public String getName() {
        return lb.getName();
    }

    public String getDnsName() {
        return lb.getDnsName();
    }

    public List<Listener> getListeners() {
        return lb.getListeners();
    }

    public List<String> getAvailabilityZones() {
        return lb.getAvailabilityZones();
    }

    public List<String> getInstances() {
        return lb.getInstances();
    }

    public HealthCheck getHealthCheck() {
        return lb.getHealthCheck();
    }

    public Calendar getCreatedTime() {
        return lb.getCreatedTime();
    }

    public int getInboundPort() throws LoadBalancingException, RemoteException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<Endpoint> getEndpoints() throws LoadBalancingException, RemoteException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void registerEndpoint(Endpoint endpoint) throws LoadBalancingException, RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void deregisterEndpoint(Endpoint endpoint) throws LoadBalancingException, RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public LoadBalancingStrategy getStrategy() throws LoadBalancingException, RemoteException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setLoadBalancingStrategy(LoadBalancingStrategy strategy) throws LoadBalancingException, RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
