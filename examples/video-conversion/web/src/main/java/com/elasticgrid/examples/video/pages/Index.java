/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * Licensed under the GNU Lesser General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.elasticgrid.examples.video.pages;

import com.elasticgrid.examples.video.VideoConverter;
import com.elasticgrid.examples.video.util.ServiceLocator;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceItem;
import org.apache.tapestry5.annotations.Property;
import org.rioproject.cybernode.Cybernode;
import org.rioproject.watch.WatchDataSource;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class Index {
    @Property
    private long approximateBacklog;

    @Property
    private List<WatchDataSource> watches;

    @Property
    private Map<ServiceID, List<WatchDataSource>> servicesWatches;

    @Property
    private Map<ServiceID, List<WatchDataSource>> cybernodesWatches;

    @Property
    private ServiceID service;

    @Property
    private WatchDataSource watch;

    private VideoConverter converter;

    //private ProvisionMonitor monitor;

    private static final Logger logger = Logger.getLogger(Index.class.getName());

    void onActivate() throws RemoteException, InterruptedException {
        ServiceItem[] serviceItems = ServiceLocator.getServicesByClass(VideoConverter.class);
        if (serviceItems != null && serviceItems.length > 1)
            converter = (VideoConverter) serviceItems[0].service;
        else
            logger.warning("Could not find any video conversion service");
        //monitor = (ProvisionMonitor) ServiceLocator.getServicesByClass(ProvisionMonitor.class)[0].service;
        servicesWatches = ServiceLocator.getWatchDataSources(VideoConverter.class);
        cybernodesWatches = ServiceLocator.getWatchDataSources(Cybernode.class);
        approximateBacklog = converter != null ? converter.getApproximateBacklog() : 0;
    }

    public Set<ServiceID> getServices() {
        return servicesWatches.keySet();
    }

    public Set<ServiceID> getCybernodes() {
        return cybernodesWatches.keySet();
    }

    public List<WatchDataSource> getWatchesForCurrentService() {
        return servicesWatches.get(service);
    }

    public List<WatchDataSource> getWatchesForCurrentCybernode() {
        return cybernodesWatches.get(service);
    }

    public String[] getServiceWatchContext() throws RemoteException {
        return new String[]{service.toString(), watch.getID()};
    }

    public int[] getpopupSize() {
        return new int[]{800, 600};
    }

}
