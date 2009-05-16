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
package com.elasticgrid.model;

public class NodeProfileInfo {
    private NodeProfile nodeProfile;
    private NodeType nodeType;
    private int number;
    private boolean override;

    /** Needed for JiBX */
    public NodeProfileInfo() {}

    public NodeProfileInfo(NodeProfile nodeProfile, NodeType nodeType, int number) {
        this.nodeProfile = nodeProfile;
        this.nodeType = nodeType;
        this.number = number;
        this.override = false;
    }

    public NodeProfile getNodeProfile() {
        return nodeProfile;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public int getNumber() {
        return number;
    }

    public boolean hasOverride() {
        return override;
    }

    public void setOverride(boolean override) {
        this.override = override;
    }
}
