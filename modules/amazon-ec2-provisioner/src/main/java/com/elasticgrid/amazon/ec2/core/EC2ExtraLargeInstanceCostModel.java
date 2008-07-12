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

package com.elasticgrid.amazon.ec2.core;

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