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
