/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This file is part of Elastic Grid.
 * 
 * Elastic Grid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * 
 * Elastic Grid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Elastic Grid.  If not, see <http://www.gnu.org/licenses/>.
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
