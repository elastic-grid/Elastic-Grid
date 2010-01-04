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
package com.elasticgrid.platforms.ec2.loadbalancing;

import com.elasticgrid.loadbalancing.Endpoint;
import com.elasticgrid.loadbalancing.LoadBalancerManager;
import com.elasticgrid.loadbalancing.LoadBalancingException;
import com.elasticgrid.loadbalancing.spi.LoadBalancer;
import com.xerox.amazonws.ec2.AvailabilityZone;
import com.xerox.amazonws.ec2.EC2Exception;
import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.Listener;
import com.xerox.amazonws.ec2.LoadBalancing;
import com.xerox.amazonws.ec2.ReservationDescription;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@link LoadBalancerManager} implementation based on Amazon Elastic LoadBalancing.
 *
 * @author Jerome Bernard
 */
public class ElasticLoadBalancingLoadBalancerManager implements LoadBalancerManager {
    private final LoadBalancing elb;
    private final Jec2 ec2;
    private static final Logger logger = Logger.getLogger(ElasticLoadBalancingLoadBalancerManager.class.getName());

    public ElasticLoadBalancingLoadBalancerManager(LoadBalancing elb, Jec2 ec2) {
        this.elb = elb;
        this.ec2 = ec2;
    }

    /**
     * {@inheritDoc}
     * The created load-balancer is activated only in the availability zone where the current EC2 instance is running!
     *
     * @param name the name of the load-balancer to create
     * @return the create load-balancer
     * @throws LoadBalancingException
     * @throws RemoteException
     */
    public LoadBalancer createLoadBalancer(String name, int inboundPort, int defaultOutboundPort, String protocol) throws LoadBalancingException, RemoteException {
        try {
            List<Listener> listeners = Arrays.asList(new Listener("HTTP", inboundPort, defaultOutboundPort));
            String currentZone = null;  // TODO: figure out how to retrieve this information!
            elb.createLoadBalancer(name, listeners, Collections.singletonList(currentZone), "??");// TODO: figure out why we need the dnsName for input!!!
            return getLoadBalancer(name);
        } catch (com.xerox.amazonws.ec2.LoadBalancingException e) {
            throw new LoadBalancingException("Can't create load-balancer named " + name);
//        } catch (EC2Exception e) {
//            throw new LoadBalancingException("Can't get list of EC2 availability zones", e);
        }
    }

    public LoadBalancer getLoadBalancer(String name) throws LoadBalancingException, RemoteException {
        List<com.xerox.amazonws.ec2.LoadBalancer> lbs;
        com.xerox.amazonws.ec2.LoadBalancer lb;
        try {
            lbs = elb.describeLoadBalancers(Collections.singletonList(name));
            if (lbs.size() != 1)
                throw new LoadBalancingException("Can't find a load-balancer named " + name);
            lb = lbs.get(0);
            return new ElasticLoadBalancingLoadBalancer(lb);
        } catch (com.xerox.amazonws.ec2.LoadBalancingException e) {
            throw new LoadBalancingException("Can't retrieve load-balancers description", e);
        }
    }

    /**
     * {@inheritDoc}
     * When registering an endpoint, if required the availability zone of the endpoint to register will be enabled
     * in the load-balancer.
     *
     * @param name     the name of the load-balancer to which the endpoint should be registered
     * @param endpoint the endpoint to register to the load-balancer
     * @throws LoadBalancingException
     * @throws RemoteException
     */
    public void registerEndpoint(String name, Endpoint endpoint) throws LoadBalancingException, RemoteException {
        try {
            // enable the availability zone of this endpoint if not already enabled
            List<String> currentZones = elb.describeLoadBalancers(Collections.singletonList(name)).get(0).getAvailabilityZones();
            String endpointZone = null;     // TODO: retreive this information!
            if (!currentZones.contains(endpointZone)) {
                logger.info("Enabling availability zone " + endpointZone + " for load-balancer named" + name + "...");
                elb.enableAvailabilityZonesForLoadBalancer(name, Collections.singletonList(endpointZone));
            }
            // register the endpoint to the load-balancer
            String instanceID = null;   // TODO: retrieve the currently running EC2 InstanceID
            List<String> instances = elb.registerEndPoints(name, Collections.singletonList(instanceID));
            StringBuffer buffer = new StringBuffer();
            for (String instance : instances)
                buffer.append(instance).append(' ');
            logger.log(Level.INFO, "Load-balancer {0} is now routing traffic to instances {1}",
                    new Object[]{name, buffer.toString()});
        } catch (com.xerox.amazonws.ec2.LoadBalancingException e) {
            throw new LoadBalancingException("Can't register endpoint to load-balancer named " + name, e);
        }
    }

    /**
     * {@inheritDoc}
     * When deregistering an endpoint, if required the availability zone of the endpoint to deregister will be disabled
     * from the load-balancer (depending on the other EC2 instances still registered).
     * @param name the name of the load-balancer from which the endpoint should be deregistered
     * @param endpoint the endpoint to deregister from the load-balancer
     * @throws LoadBalancingException
     * @throws RemoteException
     */
    public void deregisterEndpoint(String name, Endpoint endpoint) throws LoadBalancingException, RemoteException {
        try {
            // disable the availability zone of this endpoint if no other EC2 instances are registered in the same zone
            List<String> registeredInstances = elb.describeLoadBalancers(Collections.singletonList(name)).get(0).getInstances();
            List<String> currentZones = elb.describeLoadBalancers(Collections.singletonList(name)).get(0).getAvailabilityZones();
            String endpointZone = null;         // TODO: retrieve this information!
            String endpointInstanceID = null;   // TODO: retrieve this information!
            // remove the endpoint instanceID from the list
            registeredInstances.remove(endpointInstanceID);
            // compute the required availability zones
            Set<String> usedZones = new HashSet<String>();
            List<ReservationDescription> instancesDescriptions = ec2.describeInstances(registeredInstances);
            for (ReservationDescription reservations : instancesDescriptions) {
                List<ReservationDescription.Instance> instances = reservations.getInstances();
                for (ReservationDescription.Instance instance : instances)
                    usedZones.add(instance.getAvailabilityZone());
            }
            // clean the availability zones list so that non-required one aren't declared anymore
            for (String zone : currentZones) {
                if (!usedZones.contains(zone)) {
                    logger.info("Disabling availability zone " + endpointZone + " for load-balancer named" + name + "...");
                    elb.disableAvailabilityZones(name, Collections.singletonList(endpointZone));
                }
            }
            // deregister the endpoint from the load-balancer
            String instanceID = null;   // TODO: retrieve the currently running EC2 InstanceID
            List<String> instances = elb.deregisterInstancesFromLoadBalancer(name, Collections.singletonList(instanceID));
            StringBuffer buffer = new StringBuffer();
            for (String instance : instances)
                buffer.append(instance).append(' ');
            logger.log(Level.INFO, "Load-balancer {0} is now routing traffic to instances {1}",
                    new Object[]{name, buffer.toString()});
        } catch (com.xerox.amazonws.ec2.LoadBalancingException e) {
            throw new LoadBalancingException("Can't deregister endpoint from load-balancer named " + name, e);
        } catch (EC2Exception e) {
            throw new LoadBalancingException("Can't obtain information about EC2 instances", e);
        }
    }

    public List<LoadBalancer> getLoadBalancers() throws LoadBalancingException, RemoteException {
        List<com.xerox.amazonws.ec2.LoadBalancer> lbs;
        try {
            lbs = elb.describeLoadBalancers(null);
        } catch (com.xerox.amazonws.ec2.LoadBalancingException e) {
            throw new LoadBalancingException("Can't retrieve load-balancers description", e);
        }
        List<LoadBalancer> balancers = new ArrayList<LoadBalancer>(lbs.size());
        for (com.xerox.amazonws.ec2.LoadBalancer lb : lbs) {
            balancers.add(new ElasticLoadBalancingLoadBalancer(lb));
        }
        return balancers;
    }

}
