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

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import java.io.Serializable;
import java.util.List;

public class Cluster extends BaseTreeModel implements Serializable {

    public Cluster() {
    }

    public Cluster(String name, List<Node> nodes, List<Application> applications) {
        setName(name);
        setNodes(nodes);
        setApplications(applications);
    }

    public String getName() {
        return get("name");
    }

    public void setName(String name) {
        set("name", name);
    }

    public List<Node> getNodes() {
        return get("nodes");
    }

    public void setNodes(List<Node> nodes) {
        set("nodes", nodes);
        set("number_of_nodes", nodes.size());
    }

    public List<Application> getApplications() {
        return get("applications");
    }

    public void setApplications(List<Application> applications) {
        set("applications", applications);
        set("number_of_applications", applications.size());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Cluster");
        sb.append("{name='").append(getName()).append('\'');
        sb.append('}');
        return sb.toString();
    }

}