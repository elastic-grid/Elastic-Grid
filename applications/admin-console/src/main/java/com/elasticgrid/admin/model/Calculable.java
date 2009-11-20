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
import java.util.Date;

public class Calculable extends BaseModel implements Serializable {
    public Calculable() {
    }

    public Calculable(String id, Double value, Date when) {
        setId(id);
        setValue(value);
        setWhen(when);
    }

    public Calculable(String id, Double value, Date when, String detail) {
        setId(id);
        setValue(value);
        setWhen(when);
        setDetail(detail);
    }

    public String getId() {
        return get("id");
    }

    public void setId(String id) {
        set("id", id);
    }

    public Double getValue() {
        return get("value");
    }

    public void setValue(Double value) {
        set("value", value);
    }

    public Date getWhen() {
        return get("when");
    }

    public void setWhen(Date when) {
        set("when", when);
    }

    public String getDetail() {
        return get("detail");
    }

    public void setDetail(String detail) {
        set("detail", detail);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Calculable");
        sb.append("{id='").append(getId()).append('\'');
        sb.append(", value=").append(getValue());
        sb.append(", when=").append(getWhen());
        sb.append('}');
        return sb.toString();
    }
}
