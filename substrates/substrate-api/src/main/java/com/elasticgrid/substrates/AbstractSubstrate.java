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
package com.elasticgrid.substrates;

import org.rioproject.jsb.ServiceBeanAdapter;
import org.rioproject.core.jsb.ServiceBeanContext;
import org.rioproject.watch.Watch;
import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Collections;
import java.util.logging.Logger;
import java.sql.SQLException;

/**
 * Abstract {@link Substrate} implementation.
 */
public abstract class AbstractSubstrate extends ServiceBeanAdapter implements Substrate {
    private Set<Watch> watches = new HashSet<Watch>();
    protected Logger logger = Logger.getLogger(getClass().getName());

    protected void initialize(Configuration config) throws Exception {
        // do nothing by default -- here for subclasses
    }

    @Override
    public void initialize(ServiceBeanContext context) throws Exception {
        super.initialize(context);
        initialize(context.getConfiguration());
    }

    @Override
    public Object start(ServiceBeanContext context) throws Exception {
        super.getWatchRegistry().register(watches.toArray(new Watch[watches.size()]));
        return super.start(context);
    }

    protected void registerWatch(Watch watch) {
        watches.add(watch);
    }

    public Set<Watch> getWatches() {
        return watches;
    }

    public List<FirewallRule> getFirewallRules() {
        return Collections.emptyList();
    }
}
