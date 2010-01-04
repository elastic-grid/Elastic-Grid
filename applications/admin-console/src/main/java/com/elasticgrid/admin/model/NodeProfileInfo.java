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

public class NodeProfileInfo extends BaseModel implements Serializable {
    public static final String PROFILE_MONITOR = "monitor";
    public static final String PROFILE_AGENT = "agent";
    public static final String PROFILE_MONITOR_AND_AGENT = "monitor_and_agent";

    public NodeProfileInfo() {}

    public NodeProfileInfo(String nodeProfile, String nodeType, Integer number, Boolean override) {
        setNodeProfile(nodeProfile);
        setNodeType(nodeType);
        setNumber(number);
        setOverride(override);
    }

    public String getNodeProfile() {
        return get("node_profile");
    }

    public void setNodeProfile(String nodeProfile) {
        set("node_profile", nodeProfile);
    }

    public String getNodeType() {
        return get("node_type");
    }

    public void setNodeType(String nodeType) {
        set("node_type", nodeType);
    }

    public Integer getNumber() {
        return get("number");
    }

    public void setNumber(Integer number) {
        set("number", number);
    }

    public Boolean hasOverride() {
        return get("has_override");
    }

    public void setOverride(Boolean override) {
        set("has_override", override);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("NodeProfileInfo");
        sb.append("{nodeProfile='").append(getNodeProfile()).append('\'');
        sb.append(", nodeType='").append(getNodeType()).append('\'');
        sb.append(", number=").append(getNumber());
        sb.append(", hasOverride=").append(hasOverride());
        sb.append('}');
        return sb.toString();
    }
}