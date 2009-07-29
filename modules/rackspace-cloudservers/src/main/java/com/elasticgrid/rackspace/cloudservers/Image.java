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
package com.elasticgrid.rackspace.cloudservers;

import java.io.Serializable;
import java.util.Date;

/**
 * Image: collection of files used to create or rebuild a server.
 *
 * @author Jerome Bernard
 */
public class Image implements Serializable {
    private final Integer id;
    private final String name;
    private final Integer serverId;
    private final Date updated;
    private final Date created;
    private final Integer progress;
    private final Status status;

    public Image(Integer id, String name, Integer serverId, Date updated, Date created, Integer progress, Status status) {
        this.id = id;
        this.name = name;
        this.serverId = serverId;
        this.updated = updated;
        this.created = created;
        this.progress = progress;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getServerId() {
        return serverId;
    }

    public Date getUpdated() {
        return updated;
    }

    public Date getCreated() {
        return created;
    }

    public Integer getProgress() {
        return progress;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Image");
        sb.append("{id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", serverId=").append(serverId);
        sb.append(", updated=").append(updated);
        sb.append(", created=").append(created);
        sb.append(", progress=").append(progress);
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }

    enum Status implements Serializable {
        UNKNOWN, ACTIVE, SAVING, PREPARING, QUEUED, FAILED
    }
}
