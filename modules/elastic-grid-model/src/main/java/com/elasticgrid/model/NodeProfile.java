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

package com.elasticgrid.model;

import java.io.Serializable;

/**
 * Enum of the node profiles.
 * A node can either be a monitor or an agent or both.
 * If it's both, Elastic Grid actually starts two JVM: one for monitor and
 * another one for the agent, so that if one of them crashes, the other one
 * is not stuck too.
 * @author Jerome Bernard
 */
public enum NodeProfile implements Serializable {
    MONITOR(false, true), AGENT(true, false),
    MONITOR_AND_AGENT(true, true);

    private boolean agent;
    private boolean monitor;

    NodeProfile(boolean agent, boolean monitor) {
        this.agent = agent;
        this.monitor = monitor;
    }

    public boolean isAgent() {
        return agent;
    }

    public boolean isMonitor() {
        return monitor;
    }

}
