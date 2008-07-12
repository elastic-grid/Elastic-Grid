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

package com.elasticgrid;

import org.rioproject.resources.servicecore.AbstractProxy;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.Remote;

import net.jini.id.Uuid;

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
