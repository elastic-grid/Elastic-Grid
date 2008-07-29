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
import static java.lang.String.format;
import java.util.logging.Logger;

/**
 * {@link ResourceCostModel} for EC2 service.
 * @author Jerome Bernard
 */
public abstract class AbstractEC2InstanceCostModel implements ResourceCostModel {
    private static final Logger logger = Logger.getLogger(EC2SmallInstanceCostModel.class.getName());

    protected abstract double getCostPerHour();

    /**
     * Get the cost per unit
     * @param duration the amount of time in milliseconds that is to be used to compute the cost per unit
     * @return The cost per unit
     */
    public double getCostPerUnit(long duration) {
        double cost = duration * getCostPerHour() / 60 / 60 / 1000;
        logger.info(format("EC2 Cost for %d milliseconds is %f", duration, cost));
        return cost;
    }

    /**
     * Add a TimeBoundary to the ResourceCostModel
     * @param timeBoundary a TimeBoundary indicating the attributes to be applied to the computation of
     * cost per unit for resource use over a duration
     */
    public void addTimeBoundary(TimeBoundary timeBoundary) {
        // implemented to meet ResourceCostModel contract
    }
}