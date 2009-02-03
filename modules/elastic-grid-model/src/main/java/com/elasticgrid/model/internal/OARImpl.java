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

package com.elasticgrid.model.internal;

import com.elasticgrid.model.OAR;

/**
 * @author Jerome Bernard
 */
public class OARImpl implements OAR {
    private String name;
    private String version;
    private String opStringName;
    private String deployDir;
    private String activationType;

    public OARImpl() {
    }

    public String getName() {
        return name;
    }

    public OAR name(String name) {
        setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public OAR version(String version) {
        setVersion(version);
        return this;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOpStringName() {
        return opStringName;
    }

    public OAR opstring(String opStringName) {
        setOpStringName(opStringName);
        return this;
    }

    public void setOpStringName(String opStringName) {
        this.opStringName = opStringName;
    }

    public String getDeployDir() {
        return deployDir;
    }

    public OAR deployDir(String deployDir) {
        setDeployDir(deployDir);
        return this;
    }

    public void setDeployDir(String deployDir) {
        this.deployDir = deployDir;
    }

    public String getActivationType() {
        return activationType;
    }

    public OAR activationType(String activationType) {
        setActivationType(activationType);
        return this;
    }

    public void setActivationType(String activationType) {
        this.activationType = activationType;
    }
}
