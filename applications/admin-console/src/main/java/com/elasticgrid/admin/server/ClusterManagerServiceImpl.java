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
package com.elasticgrid.admin.server;

import com.elasticgrid.admin.client.ClusterManagerService;
import com.elasticgrid.admin.model.Application;
import com.elasticgrid.admin.model.Calculable;
import com.elasticgrid.admin.model.Cluster;
import com.elasticgrid.admin.model.Node;
import com.elasticgrid.admin.model.NodeProfileInfo;
import com.elasticgrid.admin.model.Service;
import com.elasticgrid.admin.model.Thresholds;
import com.elasticgrid.admin.model.Watch;
import com.elasticgrid.cluster.ClusterManager;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.LookupCache;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.lookup.ServiceItemFilter;
import net.jini.lookup.entry.Host;
import net.jini.lookup.entry.Name;
import org.rioproject.associations.AssociationDescriptor;
import org.rioproject.associations.AssociationMgmt;
import org.rioproject.associations.AssociationType;
import org.rioproject.cybernode.Cybernode;
import org.rioproject.monitor.ProvisionMonitor;
import org.rioproject.watch.Accumulator;
import org.rioproject.watch.WatchDataSource;
import javax.servlet.ServletException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClusterManagerServiceImpl extends RemoteServiceServlet implements ClusterManagerService {
    private static ClusterManager clusterManager;
    private ServiceDiscoveryManager sdm;
    private LookupCache agents;
    private LookupCache monitors;
    private LookupCache services;
    private static final int DISCO_TIMEOUT = 5 * 1000;      // 5 seconds
    private static final Logger logger = Logger.getLogger(ClusterManagerServiceImpl.class.getName());

    @Override
    public void init() throws ServletException {
        String discoGroup = System.getProperty("org.rioproject.groups");

        AssociationDescriptor clusterManagerAssociation = new AssociationDescriptor(AssociationType.REQUIRES);
        clusterManagerAssociation.setMatchOnName(false);
        clusterManagerAssociation.setInterfaceNames(ClusterManager.class.getName());
        clusterManagerAssociation.setGroups(discoGroup);
        AssociationMgmt assocMgt = new AssociationMgmt();
        assocMgt.register(new ClusterManagerListener());
        assocMgt.addAssociationDescriptors(clusterManagerAssociation);

        try {
            LookupDiscoveryManager ldm = new LookupDiscoveryManager(new String[]{discoGroup, "all"}, null, null);
            sdm = new ServiceDiscoveryManager(ldm, new LeaseRenewalManager());
            agents = sdm.createLookupCache(new ServiceTemplate(null,
                    new Class[]{Cybernode.class}, null), null, null);
            monitors = sdm.createLookupCache(new ServiceTemplate(null,
                    new Class[]{ProvisionMonitor.class}, null), null, null);
            services = sdm.createLookupCache(new ServiceTemplate(null,
                    new Class[]{org.rioproject.resources.servicecore.Service.class}, null), null, null);
        } catch (IOException e) {
            throw new ServletException("Unexpected I/O exception", e);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        agents.terminate();
        monitors.terminate();
        services.terminate();
        sdm.terminate();
    }

    @SuppressWarnings("unchecked")
    public List<Cluster> findClusters() {
        try {
            Set<? extends com.elasticgrid.model.Cluster> raw = getClusterManager().findClusters();
            ArrayList<Cluster> clusters = new ArrayList<Cluster>(raw.size());
            for (com.elasticgrid.model.Cluster cluster : raw) {
                // add cluster to model
                clusters.add(buildCluster(cluster));
            }
            logger.log(Level.FINE, "Found {0} clusters.", clusters.size());
            return clusters;
        } catch (Exception e) {
            throw new IllegalStateException("Can't fetch clusters", e);
        }
    }

    public Cluster findCluster(String name) {
        try {
            return buildCluster(getClusterManager().cluster(name));
        } catch (Exception e) {
            throw new IllegalStateException("Can't fetch cluster details", e);
        }
    }

    public void startCluster(String clusterName, List<NodeProfileInfo> clusterTopology) {
        // TODO: write this!
    }

    public void stopCluster(String clusterName) {
        // TODO: write this!
    }

    public void stopNode(Node node) {
        logger.info("Stopping node " + node);
        // TODO: write this!!
    }

    public Map<Node, List<Watch>> getWatchesForNodes(List<Node> nodes) {
        try {
            Map<Node, List<Watch>> watches = new HashMap<Node, List<Watch>>();
            for (Node node : nodes) {
                Cybernode cybernode = findAgentByHostAddress(node.getIP());
                WatchDataSource[] sources = cybernode.fetch();
                List<Watch> nodeWatches = new ArrayList<Watch>();
                for (WatchDataSource wds : sources) {
                    nodeWatches.add(buildWatch(wds));
                }
                watches.put(node, nodeWatches);
            }
            return watches;
        } catch (InterruptedException e) {
            logger.severe("Could not locate service in " + DISCO_TIMEOUT + " ms.");
            return Collections.emptyMap();
        } catch (RemoteException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return Collections.emptyMap();
        }
    }

    public Map<Service, List<Watch>> getWatchesForServices(List<Service> services) {
        try {
            Map<Service, List<Watch>> watches = new HashMap<Service, List<Watch>>();
            for (Service service : services) {
                org.rioproject.resources.servicecore.Service srv = findServiceByName(service.getName());
                if (srv == null) {
                    logger.warning("Could not locate service " + service.getName() + ". Skipping it...");
                    continue;
                }
                WatchDataSource[] sources = srv.fetch();
                logger.log(Level.FINE, "Service {0} has {1} watches",
                        new Object[]{service.getName(), sources.length});
                List<Watch> serviceWatches = new ArrayList<Watch>();
                for (WatchDataSource wds : sources) {
                    logger.log(Level.FINE, "Found watch {0}", wds.getID());
                    serviceWatches.add(buildWatch(wds));
                }
                watches.put(service, serviceWatches);
            }
            return watches;
        } catch (InterruptedException e) {
            logger.severe("Could not locate service in " + DISCO_TIMEOUT + " ms.");
            return Collections.emptyMap();
        } catch (RemoteException e) {
            logger.log(Level.SEVERE, "Unexpected remote exception", e);
            return Collections.emptyMap();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unexpected I/O exception", e);
            return Collections.emptyMap();
        }
    }

    public Map<Node, Watch> getWatchOnEachNode(List<Node> nodes, String id) {
        try {
            logger.log(Level.FINE, "Searching for watch with ID {0} for nodes {1}", new Object[]{id, nodes});
            Map<Node, Watch> watches = new HashMap<Node, Watch>();
            for (Node node : nodes) {
                logger.log(Level.FINE, "Searching for watches available for node {0}", node.getName());
                Cybernode cybernode = findAgentByHostAddress(node.getIP());
                if (cybernode == null) {
                    logger.log(Level.WARNING, "Could not find an agent with IP address {0}", node.getIP());
                    return null;
                }
                WatchDataSource[] sources = cybernode.fetch(id);
                Watch watch = buildWatch(sources[0]);
                logger.log(Level.FINE, "For node {0}, watch {1} was found", new Object[]{node.getName(), watch});
                watches.put(node, watch);
            }
            return watches;
        } catch (InterruptedException e) {
            logger.severe("Could not locate service in " + DISCO_TIMEOUT + " ms.");
            return Collections.emptyMap();
        } catch (RemoteException e) {
            logger.log(Level.SEVERE, "Unexpected remote exception", e);
            return Collections.emptyMap();
        }
    }

    public Map<Service, Watch> getWatchOnEachService(List<Service> services, String id) {
        try {
            logger.log(Level.FINE, "Searching for watch with ID {0} for services {1}", new Object[]{id, services});
            Map<Service, Watch> watches = new HashMap<Service, Watch>();
            for (Service service : services) {
                logger.log(Level.FINE, "Searching for watches available for service {0}", service.getName());
                List<org.rioproject.resources.servicecore.Service> instances = findServiceInstancesByName(service.getName());
                if (instances == null || instances.size() == 0) {
                    logger.log(Level.WARNING, "Could not find instances of service {0}", service.getName());
                    return null;
                }
                for (org.rioproject.resources.servicecore.Service instance : instances) {
                    WatchDataSource[] sources = instance.fetch(id);
                    if (sources == null || sources.length == 0) {
                        logger.log(Level.WARNING, "Could not find watch {0} for service {1} for service instance {2}",
                                new Object[]{id, service.getName(), instance.toString()});
                        continue;
                    }
                    Watch watch = buildWatch(sources[0]);
                    logger.log(Level.FINE, "For service {0}, watch {1} was found for service instance {2}",
                            new Object[]{service.getName(), watch, instance.toString()});
                    watches.put(service, watch);
                }
            }
            return watches;
        } catch (InterruptedException e) {
            logger.severe("Could not locate service in " + DISCO_TIMEOUT + " ms.");
            return Collections.emptyMap();
        } catch (RemoteException e) {
            logger.log(Level.SEVERE, "Unexpected remote exception", e);
            return Collections.emptyMap();
        }
    }

    @SuppressWarnings("unchecked")
    private Cybernode findAgentByHostAddress(final String ip) throws InterruptedException, RemoteException {
        if (logger.isLoggable(Level.FINEST))
            logger.log(Level.FINEST, "Searching for agent with IP address {0}", ip);
        ServiceItem serviceItem = agents.lookup(new ServiceItemFilter() {
            public boolean check(ServiceItem serviceItem) {
                List<Entry> attributes = Arrays.asList(serviceItem.attributeSets);
                return attributes.contains(new Host(ip));
            }
        });
        if (serviceItem != null)
            return (Cybernode) serviceItem.service;
        else
            return null;
    }

    @SuppressWarnings("unchecked")
    private org.rioproject.resources.servicecore.Service findServiceByName(final String serviceName) throws InterruptedException, RemoteException {
        if (logger.isLoggable(Level.FINER))
            logger.log(Level.FINER, "Searching for service with name {0}", serviceName);
        ServiceItem serviceItem = services.lookup(new ServiceItemFilter() {
            public boolean check(ServiceItem serviceItem) {
                List<Entry> attributes = Arrays.asList(serviceItem.attributeSets);
                logger.log(Level.FINEST, "Qualifying service {0}", serviceItem);
                Name name = new Name(serviceName);
                return attributes.contains(name);
            }
        });
        logger.log(Level.FINER, "Found service {0}", serviceItem);
        if (serviceItem != null)
            return (org.rioproject.resources.servicecore.Service) serviceItem.service;
        else
            return null;
    }

    @SuppressWarnings("unchecked")
    private List<org.rioproject.resources.servicecore.Service> findServiceInstancesByName(final String serviceName) throws InterruptedException, RemoteException {
        if (logger.isLoggable(Level.FINER))
            logger.log(Level.FINER, "Searching for service with name {0}", serviceName);
        ServiceItem[] servicesItems = services.lookup(new ServiceItemFilter() {
            public boolean check(ServiceItem serviceItem) {
                List<Entry> attributes = Arrays.asList(serviceItem.attributeSets);
                logger.log(Level.FINEST, "Qualifying service {0}", serviceItem);
                Name name = new Name(serviceName);
                return attributes.contains(name);
            }
        }, Integer.MAX_VALUE);
        logger.log(Level.FINER, "Found services {0}", servicesItems);
        if (servicesItems == null)
            return null;
        List<org.rioproject.resources.servicecore.Service> instances = new ArrayList<org.rioproject.resources.servicecore.Service>();
        for (ServiceItem serviceItem : servicesItems)
            instances.add((org.rioproject.resources.servicecore.Service) serviceItem.service);
        return instances;
    }

    private Watch buildWatch(WatchDataSource wds) throws RemoteException {
        String name = wds.getID();
        List<Calculable> calculables = new ArrayList<Calculable>();
        for (org.rioproject.watch.Calculable calculable : wds.getCalculable()) {
            calculables.add(new Calculable(name, calculable.getValue(), new Date(calculable.getWhen())));
        }
        Accumulator accumulator = new Accumulator(wds);
        accumulator.init();
        return new Watch(name,
                new Thresholds(
                        wds.getThresholdValues().getLowThreshold(),
                        wds.getThresholdValues().getHighThreshold()
                ),
                accumulator.median(),
                accumulator.standardDeviation(),
                calculables);
    }

    private Cluster buildCluster(com.elasticgrid.model.Cluster cluster) {
        // process nodes
        List<Node> nodes = new ArrayList<Node>(cluster.getNodes().size());
        for (Object o : cluster.getNodes()) {
            com.elasticgrid.model.Node node = (com.elasticgrid.model.Node) o;
            nodes.add(new Node(
                    node.getID(),
                    node.getAddress().getHostName(),
//                    String.format("%s (%s)", node.getAddress().getHostName(), node.getInstanceID()),
                    node.getAddress().getHostAddress(),
                    node.getProfile().name(),
                    node.getType().getName()
            ));
        }
        // process applications
        List<Application> applications = new ArrayList<Application>();
        for (Object o : cluster.getApplications()) {
            com.elasticgrid.model.Application application = (com.elasticgrid.model.Application) o;
            logger.log(Level.FINE, "Processing application {0}", application.getName());
            List<Service> services = new ArrayList<Service>(application.getServices().size());
            for (com.elasticgrid.model.Service service : application.getServices())
                services.add(buildService(service, application.getName()));
            applications.add(new Application(application.getName(), services));
        }
        return new Cluster(cluster.getName(), nodes, applications);
    }

    private Service buildService(com.elasticgrid.model.Service service, String applicationName) {
        logger.log(Level.FINE, "Processing service {0}", service.getName());
        return new Service(service.getName(), applicationName);
    }

    public synchronized static ClusterManager getClusterManager() throws InterruptedException {
        while (clusterManager == null) {
            logger.warning("Waiting for discovery of cluster manager...");
            Thread.sleep(200);
        }
        return clusterManager;
    }

    public static void setClusterManager(ClusterManager clusterManager) {
        logger.log(Level.FINE, "Found cluster manager {0}", clusterManager.getClass().getName());
        ClusterManagerServiceImpl.clusterManager = clusterManager;
    }
}
