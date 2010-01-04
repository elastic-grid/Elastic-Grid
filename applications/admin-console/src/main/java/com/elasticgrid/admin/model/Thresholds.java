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
package com.elasticgrid.admin.model;

import com.extjs.gxt.ui.client.data.BaseModel;
import java.io.Serializable;

public class Thresholds extends BaseModel implements Serializable {
    public Thresholds() {}

    public Thresholds(Double low, Double high) {
        setLow(low);
        setHigh(high);
    }

    public Double getLow() {
        return get("low");
    }

    public void setLow(Double low) {
        set("low", low);
    }

    public Double getHigh() {
        return get("high");
    }

    public void setHigh(Double low) {
        set("high", low);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Thresholds");
        sb.append("{low='").append(getLow()).append('\'');
        sb.append(", high='").append(getHigh()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
