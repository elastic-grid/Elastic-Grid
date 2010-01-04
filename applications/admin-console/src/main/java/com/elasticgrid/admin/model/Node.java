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

public class Node extends BaseModel implements Comparable, Serializable {

    public Node() {
    }

    public Node(String id, String name, String ip, String profile, String type) {
        setId(id);
        setName(name);
        setIP(ip);
        setProfile(profile);
        setType(type);
    }

    public String getId() {
        return get("id");
    }

    public void setId(String id) {
        set("id", id);
    }

    public String getName() {
        return get("name");
    }

    public void setName(String name) {
        set("name", name);
    }

    public String getIP() {
        return get("ip");
    }

    public void setIP(String ip) {
        set("ip", ip);
    }

    public String getProfile() {
        return get("profile");
    }

    public void setProfile(String profile) {
        set("profile", profile);
    }

    public String getType() {
        return get("type");
    }

    public void setType(String type) {
        set("type", type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return getId().equals(node.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    public int compareTo(Object o) {
        if (!(o instanceof Node))
            return -1;
        Node node = (Node) o;
        return getId().compareTo(node.getId());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Node");
        sb.append("{id='").append(getId()).append('\'');
        sb.append(", name='").append(getName()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}