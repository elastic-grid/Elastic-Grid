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

package com.elasticgrid.grid.ec2;

/**
 * Enum of the type of Amazon EC2 instances.
 */
public enum InstanceType {
    SMALL("small"),
    LARGE("large"), EXTRA_LARGE("extra large"),
    MEDIUM_HIGH_CPU("medium_hcpu"), EXTRA_LARGE_HIGH_CPU("xlarge_hcpu");

    private String name;

    InstanceType(String name) {
        this.name = name;
    }

    public Object getName() {
        return name;
    }

    public String toString() {
        return "InstanceType{" +
                "name='" + name + '\'' +
                '}';
    }
}
