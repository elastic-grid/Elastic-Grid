/**
 * Elastic Grid
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
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

package com.elasticgrid.examples.video.components;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;

import java.rmi.RemoteException;

public class Watch {
    @Property
    @Parameter(required = true)
    private String service;

    @Property
    @Parameter(required = true)
    private String watch;

    @Property
    @Persist
    private boolean windowState;
    
    public String[] getServiceWatchContext() throws RemoteException {
        return new String[] { service, watch };
    }

    public int[] getpopupSize() {
        return new int[]{800, 600};
    }

}
