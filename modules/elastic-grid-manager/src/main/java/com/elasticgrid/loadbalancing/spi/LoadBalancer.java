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
package com.elasticgrid.loadbalancing.spi;

import com.elasticgrid.loadbalancing.Endpoint;
import com.elasticgrid.loadbalancing.LoadBalancingStrategy;
import com.elasticgrid.loadbalancing.LoadBalancingException;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Load Balancer redirecting traffic to other services.
 * The load-balancer needs to know:
 * <ul>
 *   <li>on which port it should bind for incoming traffic,</li>
 *   <li>the list of underlying services where the traffic should be routed,</li>
 * </ul>
 * The load-balancer whenever possible should be dynamically updated when the cluster topology changes.
 * @author Jerome Bernard
 */
public interface LoadBalancer {

    /**
     * Return the port used by the load-balancer for incoming traffic
     * @return the port
     * @throws RemoteException
     */
    int getInboundPort() throws LoadBalancingException, RemoteException;

    /**
     * Return the list of available endpoints to where the traffic can be routed
     * @return the list of endpoints
     * @throws RemoteException
     */
    List<Endpoint> getEndpoints() throws LoadBalancingException, RemoteException;

    /**
     * Register a new {@link Endpoint} to which incoming traffic can be routed.
     * @param endpoint the endpoint to register
     * @throws RemoteException
     */
    void registerEndpoint(Endpoint endpoint) throws LoadBalancingException, RemoteException;

    /**
     * Deregister an {@link Endpoint} so that incoming traffic is not routed anymore to this endpoint.
     * @param endpoint the endpoint to deregister
     * @throws RemoteException
     */
    void deregisterEndpoint(Endpoint endpoint) throws LoadBalancingException, RemoteException;

    /**
     * Return the load-balancing strategy used by the load-balancer.
     * @return the strategy
     * @throws RemoteException
     */
    LoadBalancingStrategy getStrategy() throws LoadBalancingException, RemoteException;

    /**
     * Set the load-balancing strategy used by the load-balancer.
     * @param strategy the strategy to use
     * @throws RemoteException
     */
    void setLoadBalancingStrategy(LoadBalancingStrategy strategy) throws LoadBalancingException, RemoteException;

}
