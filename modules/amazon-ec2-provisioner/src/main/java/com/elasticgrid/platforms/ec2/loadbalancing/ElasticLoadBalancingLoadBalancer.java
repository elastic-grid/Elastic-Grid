package com.elasticgrid.platforms.ec2.loadbalancing;

import com.elasticgrid.loadbalancing.Endpoint;
import com.elasticgrid.loadbalancing.LoadBalancingStrategy;
import com.elasticgrid.loadbalancing.LoadBalancingException;
import com.elasticgrid.loadbalancing.spi.LoadBalancer;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Load Balancer built on top of Amazon Elastic LoadBalancing service.
 * @author Jerome Bernard
 */
public class ElasticLoadBalancingLoadBalancer implements LoadBalancer {
    private final com.xerox.amazonws.ec2.LoadBalancer lb;
    private final ElasticLoadBalancingLoadBalancerManager elb;

    public ElasticLoadBalancingLoadBalancer(String name, ElasticLoadBalancingLoadBalancerManager elb) throws LoadBalancingException, RemoteException {
        this.elb = elb;
        this.lb = elb.getLoadBalancer(name);
    }

    public ElasticLoadBalancingLoadBalancer(com.xerox.amazonws.ec2.LoadBalancer lb, ElasticLoadBalancingLoadBalancerManager elb) {
        this.lb = lb;
        this.elb = elb;
    }

    public int getInboundPort() throws RemoteException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<Endpoint> getEndpoints() throws RemoteException {

    }

    public void registerEndpoint(Endpoint endpoint) throws RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void deregisterEndpoint(Endpoint endpoint) throws RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public LoadBalancingStrategy getStrategy() throws RemoteException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setLoadBalancingStrategy(LoadBalancingStrategy strategy) throws RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
