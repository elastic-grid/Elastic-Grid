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

package com.elasticgrid.monitor;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EC2ProvisionMonitor extends Remote {
    /**
     * Starts an Amazon EC2 instance.
     * The instance will probably not be in status <em>running</em> when the method returns.
     * The client of this method should check the status based on the return <em>instance ID</em>.
     * @param imageID the Amazon EC2 image ID
     * @return the ID of the instance
     * @throws java.rmi.RemoteException if there is a network failure
     */
    String startInstance(String imageID) throws RemoteException;

    /**
     * Stops an Amazon EC2 instance.
     * @param instanceID the Amazon EC2 instance ID to shutdown
     * @throws RemoteException if there is a network failure
     */
    void shutdownInstance(String instanceID) throws RemoteException;
}
