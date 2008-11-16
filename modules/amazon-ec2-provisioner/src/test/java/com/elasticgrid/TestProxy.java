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

package com.elasticgrid;

import net.jini.id.Uuid;
import org.rioproject.resources.servicecore.AbstractProxy;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class TestProxy extends AbstractProxy implements TestInterface, Serializable {
    private TestInterface server;

    public TestProxy(Remote remote, Uuid uuid) {
        super(remote, uuid);
        this.server = (TestInterface) remote;
    }

    public String echo(String message) throws RemoteException {
        return server.echo(message);
    }
}
