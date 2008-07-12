/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * Licensed under the GNU Lesser General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
