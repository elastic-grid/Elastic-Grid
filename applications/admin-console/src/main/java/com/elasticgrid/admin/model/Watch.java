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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Watch extends BaseModel implements Serializable {
    public Watch() {
    }

    public Watch(String name, Calculable... calculables) {
        this(name, null, 0, 0, Arrays.asList(calculables));
    }

    public Watch(String name, Thresholds thresholds, double median, double stdDev, List<Calculable> calculables) {
        setName(name);
        setThresholds(thresholds);
        setMedian(median);
        setStandardDeviation(stdDev);
        setCalculables(calculables);
    }

    public String getName() {
        return get("name");
    }

    public void setName(String name) {
        set("name", name);
    }

    public Thresholds getThresholds() {
        return get("thresholds");
    }

    public void setThresholds(Thresholds thresholds) {
        set("thresholds", thresholds);
    }

    public Double getMedian() {
        return get("median");
    }

    public void setMedian(Double median) {
        set("median", median);
    }

    public Double getStandardDeviation() {
        return get("std_dev");
    }

    public void setStandardDeviation(Double stdDev) {
        set("std_dev", stdDev);
    }

    public List<Calculable> getCalculables() {
        return get("calculables");
    }

    public void setCalculables(List<Calculable> calculables) {
        set("calculables", calculables);
    }

    public void addCalculables(Calculable... calculables) {
        List<Calculable> newCalculables = new ArrayList<Calculable>(getCalculables());
        Collections.addAll(newCalculables, calculables);
        setCalculables(newCalculables);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Watch)) return false;
        Watch watch = (Watch) o;
        return getName().equals(watch.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Watch");
        sb.append("{name='").append(getName()).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
