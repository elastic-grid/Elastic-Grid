/**
 * Elastic Grid
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
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

package com.elasticgrid.test;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.lookup.LookupCache;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.lookup.ServiceItemFilter;
import java.io.IOException;
import static java.lang.String.format;
import java.net.URL;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.logging.Logger;

public abstract class AbstractJiniTest {
    private LookupCache lookupCache;
    private ServiceDiscoveryManager sdm;
    private final Logger logger = Logger.getLogger(getClass().getName());

    @SuppressWarnings("unchecked")
    protected <T> T getService(final Class<T> serviceClass) throws InterruptedException, RemoteException {
        logger.info(format("Searching for service with interface %s", serviceClass.getName()));
        ServiceItem item = lookupCache.lookup(new ServiceItemFilter() {
            public boolean check(ServiceItem serviceItem) {
                logger.info(format("Testing %s", serviceItem));
                return serviceClass.isAssignableFrom(serviceItem.service.getClass());
            }
        });
        T proxy = null;
        if (item != null)
            proxy = (T) item.service;
        if (proxy == null) {
            logger.warning(format("Could not find a service with interface %s in cache", serviceClass.getName()));
            item = sdm.lookup(new ServiceTemplate(null, new Class[]{serviceClass}, null), null, 5 * 1000);
            if (item != null)
                proxy = (T) item.service;
        }
        assert proxy != null : format("Couldn't find proxy for service %s", serviceClass.getName());
        logger.info(format("Found proxy %s for service %s", proxy, serviceClass.getName()));
        return proxy;
    }

    @BeforeClass(groups = { "qa" })
    public void setupTest() throws ConfigurationException, IOException {
        logger.info("Preparing cache of Jini services");
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new RMISecurityManager());
        URL resource = Thread.currentThread().getContextClassLoader().getResource("jini.config");
        if (resource == null)
            logger.warning("Could not find service discovery settings... Using default settings.");
        else
            logger.info("Using service discovery settings specified in " + resource.getFile());
        Configuration config = ConfigurationProvider.getInstance(new String[]{
                resource != null ? resource.getFile() : null});
        sdm = new ServiceDiscoveryManager(null, null, config);
        ServiceTemplate ALL_SERVICES_TEMPLATE = new ServiceTemplate(null, null, null);
        lookupCache = sdm.createLookupCache(ALL_SERVICES_TEMPLATE, null, new DebugServiceDiscoveryListener());
    }

    @AfterClass(groups = { "qa" })
    public void stopJiniEnvironment() {
        logger.info("Stopping Jini environment...");
        lookupCache.terminate();
        sdm.terminate();
    }

}
