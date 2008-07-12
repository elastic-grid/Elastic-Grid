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

import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import java.util.logging.Logger;
import static java.lang.String.format;

public class DebugDiscoveryListener implements DiscoveryListener {
    private final Logger logger = Logger.getLogger(getClass().getName());

    public void discovered(DiscoveryEvent event) {
        logger.info(format("Discovery for groups %s and registrars %s",
                event.getGroups(), event.getRegistrars()));
    }

    public void discarded(DiscoveryEvent event) {
        logger.info(format("Discovery for groups %s and registrars %s",
               event.getGroups(), event.getRegistrars()));
    }
}
