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
package com.elasticgrid.model.internal;

import com.elasticgrid.model.Application;
import com.elasticgrid.model.Service;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Jerome Bernard
 */
public class ApplicationImpl implements Application {
    private String name;
    private Set<Service> services = new HashSet<Service>();

    public String getName() {
        return name;
    }

    public Application name(String name) {
        this.name = name;
        return this;
    }

    public Set<Service> getServices() {
        return services;
    }

    public Service service(String name) {
        Service service = new ServiceImpl(name);
        services.add(service);
        return service;
    }
}
