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
package com.elasticgrid.platforms.ec2;

import java.util.concurrent.Callable;
import java.rmi.RemoteException;

public class StopInstanceTask implements Callable<Void> {
    private EC2NodeInstantiator nodeInstantiator;
    private String instanceID;

    public StopInstanceTask(EC2NodeInstantiator nodeInstantiator, String instanceID) {
        this.nodeInstantiator = nodeInstantiator;
        this.instanceID = instanceID;
    }

    public Void call() throws RemoteException {
        nodeInstantiator.shutdownInstance(instanceID);
        return null;
    }
}
