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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Class used in order to load Elastic Grid repositories configurations using JiBX.
 * @author Jerome Bernard
 */
public abstract class AbstractStore {
    private Set<String> grids = setOfGrids();
    private Set<String> applications = setOfApplications();

    public AbstractStore() {
    }

    private static Set<String> setOfGrids() {
        return Collections.synchronizedSet(new HashSet<String>());
    }

    private static Set<String> setOfApplications() {
        return Collections.synchronizedSet(new HashSet<String>());
    }

    public AbstractStore grid(String name) {
        getGrids().add(name);
        return this;
    }

    public AbstractStore application(String name) {
        getApplications().add(name);
        return this;
    }

    public Set<String> getGrids() {
        return grids;
    }

    public void setGrids(Set<String> grids) {
        this.grids = grids;
    }

    public Set<String> getApplications() {
        return applications;
    }

    public void setApplications(Set<String> applications) {
        this.applications = applications;
    }
}