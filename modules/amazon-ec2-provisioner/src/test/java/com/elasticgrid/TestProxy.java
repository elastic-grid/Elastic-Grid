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
