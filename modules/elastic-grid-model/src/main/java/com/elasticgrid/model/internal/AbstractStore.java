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
package com.elasticgrid.model.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Class used in order to load Elastic Grid repositories configurations using JiBX.
 * @author Jerome Bernard
 */
public abstract class AbstractStore {
    private Set<String> clusters = setOfClusters();
    private Set<String> applications = setOfApplications();

    public AbstractStore() {
    }

    private static Set<String> setOfClusters() {
        return Collections.synchronizedSet(new HashSet<String>());
    }

    private static Set<String> setOfApplications() {
        return Collections.synchronizedSet(new HashSet<String>());
    }

    public AbstractStore cluster(String name) {
        getClusters().add(name);
        return this;
    }

    public AbstractStore application(String name) {
        getApplications().add(name);
        return this;
    }

    public Set<String> getClusters() {
        return clusters;
    }

    public void setClusters(Set<String> clusters) {
        this.clusters = clusters;
    }

    public Set<String> getApplications() {
        return applications;
    }

    public void setApplications(Set<String> applications) {
        this.applications = applications;
    }
}