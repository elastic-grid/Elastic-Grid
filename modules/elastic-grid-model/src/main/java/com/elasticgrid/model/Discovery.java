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
package com.elasticgrid.model;

/**
 * Constants related to discovery.
 * This enum exposes the name of the EC2 security groups used on EC2 and may
 * be used for other things.
 * This enum is a bit different than {@link NodeProfile} because
 * {@link NodeProfile#MONITOR_AND_AGENT} is actually both
 * {@link Discovery#MONITOR} and {@link Discovery#AGENT}.
 * @see NodeProfile
 */
public enum Discovery {
    MONITOR("eg-monitor"), AGENT("eg-agent");

    private final String groupName;

    Discovery(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }
}
