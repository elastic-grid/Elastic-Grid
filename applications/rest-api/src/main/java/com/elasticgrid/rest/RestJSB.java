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

package com.elasticgrid.rest;

import org.rioproject.core.jsb.ServiceBeanContext;
import org.rioproject.jsb.ServiceBeanAdapter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * JSB exposing the underlying REST API.
 */
public class RestJSB extends ServiceBeanAdapter {
    private ConfigurableApplicationContext springContext;

    @Override
    public void initialize(ServiceBeanContext context) throws Exception {
        springContext = new ClassPathXmlApplicationContext("/com/elasticgrid/rest/applicationContext.xml");
    }

    @Override
    public void stop(boolean force) {
        springContext.close();
    }
}
