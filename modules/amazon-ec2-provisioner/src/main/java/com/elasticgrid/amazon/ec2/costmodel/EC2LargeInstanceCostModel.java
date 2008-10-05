/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This file is part of Elastic Grid.
 * 
 * Elastic Grid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * 
 * Elastic Grid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Elastic Grid.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elasticgrid.amazon.ec2.costmodel;

import org.rioproject.costmodel.ResourceCostModel;

/**
 * {@link ResourceCostModel} for EC2 service.
 * @author Jerome Bernard
 */
public class EC2LargeInstanceCostModel extends AbstractEC2InstanceCostModel implements ResourceCostModel {
    private static final double COST_PER_HOUR = .40;        // in dollars

    public double getCostPerHour() {
        return COST_PER_HOUR;
    }

    /**
     * Get the description of the ResourceCostModel
     * @return String the description of the ResourceCostModel
     */
    public String getDescription() {
        return "EC2 Large Instance Cost Model";
    }
}