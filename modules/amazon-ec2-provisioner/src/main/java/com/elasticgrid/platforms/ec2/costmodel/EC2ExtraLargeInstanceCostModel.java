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
package com.elasticgrid.platforms.ec2.costmodel;

import org.rioproject.costmodel.ResourceCostModel;

/**
 * {@link ResourceCostModel} for EC2 service.
 * @author Jerome Bernard
 */
public class EC2ExtraLargeInstanceCostModel extends AbstractEC2InstanceCostModel implements ResourceCostModel {
    private static final double COST_PER_HOUR = .80;        // in dollars

    public double getCostPerHour() {
        return COST_PER_HOUR;
    }

    /**
     * Get the description of the ResourceCostModel
     * @return String the description of the ResourceCostModel
     */
    public String getDescription() {
        return "EC2 Extra Large Instance Cost Model";
    }
}