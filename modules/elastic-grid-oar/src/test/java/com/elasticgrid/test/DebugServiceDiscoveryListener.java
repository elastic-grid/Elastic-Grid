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

import net.jini.lookup.ServiceDiscoveryListener;
import net.jini.lookup.ServiceDiscoveryEvent;
import net.jini.core.lookup.ServiceItem;

import java.util.logging.Logger;
import static java.lang.String.format;

public class DebugServiceDiscoveryListener implements ServiceDiscoveryListener {
    private final Logger logger = Logger.getLogger(getClass().getName());

    public void serviceAdded(ServiceDiscoveryEvent event) {
        ServiceItem service = event.getPostEventServiceItem();
        logger.info(format("Discovered service %s (%s)", service.service.getClass().getName(), service.serviceID));
    }

    public void serviceRemoved(ServiceDiscoveryEvent event) {
        ServiceItem service = event.getPreEventServiceItem();
        logger.info(format("Discarded service %s", service.serviceID));
    }

    public void serviceChanged(ServiceDiscoveryEvent event) {
        ServiceItem service = event.getPostEventServiceItem();
        logger.info(format("Changed service %s", service.serviceID));
    }
}
