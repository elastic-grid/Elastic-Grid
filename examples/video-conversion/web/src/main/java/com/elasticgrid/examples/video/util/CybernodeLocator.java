/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This file is part of Elastic Grid.
 * 
 * Elastic Grid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * 
 * Elastic Grid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Elastic Grid.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elasticgrid.examples.video.util;

import org.rioproject.watch.WatchDataSource;
import org.rioproject.cybernode.Cybernode;
import org.rioproject.resources.client.DiscoveryManagementPool;
import org.rioproject.resources.client.LookupCachePool;
import org.rioproject.opstring.OpStringManagerProxy;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.security.Permission;
import java.util.*;
import java.io.IOException;

import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceID;
import net.jini.discovery.DiscoveryManagement;
import net.jini.lookup.LookupCache;
import net.jini.config.ConfigurationException;

public class CybernodeLocator {
    private static List<ServiceItem> cybernodes = Collections.synchronizedList(new ArrayList<ServiceItem>());
    private static LookupCache lookupCache;

    static {
        System.setSecurityManager(new RMISecurityManager() {
            public void checkPermission(Permission perm) {
                // do nothing -- hence approve everything
            }
        });
        try {
            ServiceTemplate template = new ServiceTemplate(null, new Class[]{Cybernode.class}, null);
            DiscoveryManagement discoMgr = DiscoveryManagementPool.getInstance().getDiscoveryManager(
                    null,
                    new String[]{"rio", "javaone"},
                    null
            );
            lookupCache = LookupCachePool.getInstance().getLookupCache(discoMgr, template);
            while (lookupCache.lookup(null) == null) {
                System.out.printf("Waiting for Cybernode(s)...\n");
                Thread.sleep(1000);
            }
            OpStringManagerProxy.setDiscoveryManagement(discoMgr);
        } catch (ConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static List<ServiceItem> getCybernodes() throws InterruptedException {
        ServiceItem[] items = lookupCache.lookup(null, Integer.MAX_VALUE);
        cybernodes.clear();
        cybernodes.addAll(Arrays.asList(items));
        Thread.sleep(1000);
        return cybernodes;
    }

    public static Map<ServiceID, List<WatchDataSource>> getWatchDataSources() throws RemoteException, InterruptedException {
        Map<ServiceID, List<WatchDataSource>> watches = new HashMap<ServiceID, List<WatchDataSource>>();
        for (ServiceItem item : getCybernodes()) {
            Cybernode cybernode = (Cybernode) item.service;
            System.out.println("Adding watches for cybernode " + cybernode.getName());
            watches.put(item.serviceID, Arrays.asList(cybernode.fetch()));
        }
        System.out.println("Found " + watches.size() + " watches");
        return watches;
    }

}
