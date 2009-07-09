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
package com.elasticgrid.loadbalancing;

import com.elasticgrid.loadbalancing.spi.LoadBalancer;
import java.rmi.RemoteException;
import java.util.List;

/**
 * The load-balancer Manager is in charge of the creation of load-balancers.
 * @author Jerome Bernard
 */
public interface LoadBalancerManager {

    /**
     * Create a new load-balancer.
     * The name may be ignored depending on underlying load-balancer implementations.
     * @param name the name of the load-balancer to create
     * @param inboundPort the port to which the load-balancer will be bound for incoming traffic
     * @param defaultOutboundPort the port used by default for outbound communication
     * @param protocol the protocol relayed by the load-balancer; may be optional depending on underlying load-balancer implementations
     * @return the created load-balancer
     * @throws LoadBalancingException
     * @throws RemoteException
     */
    LoadBalancer createLoadBalancer(String name, int inboundPort, int defaultOutboundPort, String protocol) throws LoadBalancingException, RemoteException;

    /**
     * Retrieve load-balancer information/details
     * @param name the name of the load-balancer for which information should gathered
     * @return the load-balancer details
     * @throws LoadBalancingException
     * @throws RemoteException
     */
    LoadBalancer getLoadBalancer(String name) throws LoadBalancingException, RemoteException;

    /**
     * Register a new {@link Endpoint} to an already created {@link LoadBalancer}.
     * @param name the name of the load-balancer to which the endpoint should be registered
     * @param endpoint the endpoint to register to the load-balancer
     * @throws LoadBalancingException
     * @throws RemoteException
     */
    void registerEndpoint(String name, Endpoint endpoint) throws LoadBalancingException, RemoteException;

    /**
     * Deregister an {@link Endpoint} from an already created {@link LoadBalancer}.
     * @param name the name of the load-balancer from which the endpoint should be deregistered
     * @param endpoint the endpoint to deregister from the load-balancer
     * @throws LoadBalancingException
     * @throws RemoteException
     */
    void deregisterEndpoint(String name, Endpoint endpoint) throws LoadBalancingException, RemoteException;

    /**
     * Return the list of available load-balancers.
     * @return the load-balancers
     * @throws LoadBalancingException
     * @throws RemoteException
     */
    List<LoadBalancer> getLoadBalancers() throws LoadBalancingException, RemoteException;

}