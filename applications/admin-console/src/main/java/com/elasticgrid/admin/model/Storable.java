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

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import java.io.Serializable;
import java.util.Date;

public class Storable extends BaseTreeModel implements Serializable {

    public Storable() {
    }

    public Storable(String name, Date lastModificationDate) {
        setName(name);
        setLastModificationDate(lastModificationDate);
    }

    public String getName() {
        return get("name");
    }

    public void setName(String name) {
        set("name", name);
    }

    public Date getLastModifiedDate() {
        return get("last_modification_date");
    }

    public void setLastModificationDate(Date date) {
        set("last_modification_date", date);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Storable");
        sb.append("{name='").append(getName()).append('\'');
        sb.append('}');
        return sb.toString();
    }

}