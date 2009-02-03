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

import com.elasticgrid.model.Application;
import com.elasticgrid.model.OAR;

/**
 * @author Jerome Bernard
 */
public class ApplicationImpl implements Application {
    private String name;
    private OAR oar;

    public String getName() {
        return name;
    }

    public OAR createNewOAR() {
        oar = new OARImpl();
        return oar;
    }

    public OAR oar() {
        return oar;
    }

    public Application name(String name) {
        this.name = name;
        return this;
    }
}
