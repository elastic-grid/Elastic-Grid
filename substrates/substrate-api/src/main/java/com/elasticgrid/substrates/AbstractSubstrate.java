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

package com.elasticgrid.substrates;

import org.rioproject.jsb.ServiceBeanAdapter;
import org.rioproject.core.jsb.ServiceBeanContext;
import org.rioproject.watch.Watch;
import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;

import java.util.Set;
import java.util.HashSet;
import java.util.logging.Logger;
import java.sql.SQLException;

/**
 * Abstract {@link Substrate} implementation.
 */
public abstract class AbstractSubstrate extends ServiceBeanAdapter {
    private Set<Watch> watches = new HashSet<Watch>();
    protected Logger logger = Logger.getLogger(getClass().getName());

    protected abstract void initialize(Configuration config) throws ConfigurationException, ClassNotFoundException, SQLException, Exception;

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
}
