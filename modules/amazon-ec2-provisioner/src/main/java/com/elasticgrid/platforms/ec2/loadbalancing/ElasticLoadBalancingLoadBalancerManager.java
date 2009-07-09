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
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

    public ElasticLoadBalancingLoadBalancerManager(String awsAccessID, String awsSecretKey) {
        this.elb = new LoadBalancing(awsAccessID, awsSecretKey, true);
        this.ec2 = new Jec2(awsAccessID, awsSecretKey, true);
    }

    /**
     * {@inheritDoc}
     * The created load-balancer is activated for all availability zones.
     *
     * @param name the name of the load-balancer to create
     * @return the create load-balancer
     * @throws LoadBalancingException
     * @throws RemoteException
     */
    public LoadBalancer createLoadBalancer(String name, int inboundPort, int defaultOutboundPort, String protocol) throws LoadBalancingException, RemoteException {
        try {
            List<Listener> listeners = Arrays.asList(new Listener("HTTP", inboundPort, defaultOutboundPort));
            List<AvailabilityZone> ec2zones = ec2.describeAvailabilityZones(null);
            List<String> zones = new ArrayList<String>(ec2zones.size());
            for (AvailabilityZone zone : ec2zones)
                zones.add(zone.getName());
            elb.createLoadBalancer(name, listeners, zones, "??");// TODO: figure out why we need the dnsName for input!!!
            return getLoadBalancer(name);
        } catch (com.xerox.amazonws.ec2.LoadBalancingException e) {
            throw new LoadBalancingException("Can't create load-balancer named " + name);
        } catch (EC2Exception e) {
            throw new LoadBalancingException("Can't get list of EC2 availability zones", e);
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
            return new ElasticLoadBalancingLoadBalancer(lb, this);
        } catch (com.xerox.amazonws.ec2.LoadBalancingException e) {
            throw new LoadBalancingException("Can't retrieve load-balancers description", e);
        }
    }

    public void registerEndpoint(String name, Endpoint endpoint) throws LoadBalancingException, RemoteException {
        try {
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

    public void deregisterEndpoint(String name, Endpoint endpoint) throws LoadBalancingException, RemoteException {
        try {
            String instanceID = null;   // TODO: retrieve the currently running EC2 InstanceID
            List<String> instances = elb.deregisterInstancesFromLoadBalancer(name, Collections.singletonList(instanceID));
            StringBuffer buffer = new StringBuffer();
            for (String instance : instances)
                buffer.append(instance).append(' ');
            logger.log(Level.INFO, "Load-balancer {0} is now routing traffic to instances {1}",
                    new Object[]{name, buffer.toString()});
        } catch (com.xerox.amazonws.ec2.LoadBalancingException e) {
            throw new LoadBalancingException("Can't deregister endpoint from load-balancer named " + name, e);
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
            balancers.add(new ElasticLoadBalancingLoadBalancer(lb.getName(), elb));
        }
        return balancers;
    }

}
