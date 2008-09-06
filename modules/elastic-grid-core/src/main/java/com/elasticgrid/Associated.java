/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This file is part of Elastic Grid.
 * 
 * Elastic Grid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * Elastic Grid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Elastic Grid.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elasticgrid;

import org.rioproject.associations.ServiceSelectionStrategy;
import org.rioproject.associations.AssociationType;
import org.rioproject.associations.strategy.FailOver;
import java.lang.annotation.*;

/**
 * @author Jerome Bernard
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Associated {
    String name() default "";
    Class serviceInterface();
    String[] groups() default "";
    AssociationType type() default AssociationType.USES;
    boolean matchOnName() default false;
    String proxyType() default "jdk";
    Class<? extends ServiceSelectionStrategy> serviceSelectionStrategy() default FailOver.class;
}