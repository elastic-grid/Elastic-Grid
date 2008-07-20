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

package com.elasticgrid.amazon.ec2;

import java.rmi.RemoteException;
import java.util.List;

public interface EC2Instantiator {

    /**
     * Starts Amazon EC2 instances.
     * The instance will probably not be in status <em>running</em> when the method returns.
     * The client of this method should check the status based on the return <em>instance ID</em>.
     * @param imageID the Amazon EC2 image ID
     * @param minCount the minimum number of instances to start
     * @param maxCount the maximum number of instances to start
     * @param groupSet the list of security groups to use with the new instances
     * @param userData the user data
     * @param keyName the name of the security keypair
     * @param publicAddress <tt>true</tt> if the instances should also have public IP addresses
     * @param instanceType the type of instance to start (small, large, extra large) 
     * @return the IDs of the instances
     * @throws RemoteException if there is a network failure
     */
    List<String> startInstances(String imageID, int minCount, int maxCount,
                                List<String> groupSet, String userData, String keyName,
                                boolean publicAddress, InstanceType instanceType) throws RemoteException;

    /**
     * Stops an Amazon EC2 instance.
     * @param instanceID the Amazon EC2 instance ID to shutdown
     * @throws RemoteException if there is a network failure
     */
    void shutdownInstance(String instanceID) throws RemoteException;

    List<String> getGroupsNames() throws RemoteException;
    void createGridGroup(String gridName) throws RemoteException;
}
