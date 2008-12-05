/**
 * Elastic Grid
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
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

package com.elasticgrid;

import org.rioproject.core.ServiceElement;

import java.lang.annotation.*;

/**
 * @author Jerome Bernard
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServiceBean {
    String name();
    String comment() default "";
    String group() default "rio";
    ServiceElement.ProvisionType provisionType() default ServiceElement.ProvisionType.DYNAMIC;
    ServiceElement.MachineBoundary machineBoundary() default ServiceElement.MachineBoundary.VIRTUAL;
}
