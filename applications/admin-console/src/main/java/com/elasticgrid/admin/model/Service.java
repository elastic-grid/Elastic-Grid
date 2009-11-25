/**
 * Elastic Grid
 * Copyright (C) 2008-2009 Elastic Grid, LLC.
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
package com.elasticgrid.admin.model;

import com.extjs.gxt.ui.client.data.BaseModel;
import java.io.Serializable;

public class Service extends BaseModel implements Comparable, Serializable {

    public Service() {
    }

    public Service(String name, String application) {
        setName(name);
        setApplication(application);
    }

    public String getName() {
        return get("name");
    }

    public void setName(String name) {
        set("name", name);
    }

    public String getApplication() {
        return get("application");
    }

    public void setApplication(String application) {
        set("application", application);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Service)) return false;
        Service service = (Service) o;
        return getName().equals(service.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    public int compareTo(Object o) {
        if (!(o instanceof Service))
            return -1;
        Service service = (Service) o;
        return getName().compareTo(service.getName());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Service");
        sb.append("{name='").append(getName()).append('\'');
        sb.append('}');
        return sb.toString();
    }

}