/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This file is part of Elastic Grid.
 * 
 * Elastic Grid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * Elastic Grid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Elastic Grid.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elasticgrid.grid;

import com.elasticgrid.model.NodeProfile;
import com.elasticgrid.model.Node;
import java.util.List;
import java.rmi.RemoteException;

public interface NodeInstantiator<N extends Node> {

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
     * @param options any specific {@link NodeInstantiator} option 
     * @return the IDs of the instances
     * @throws java.rmi.RemoteException if there is a network failure
     */
    List<String> startInstances(String imageID, int minCount, int maxCount,
                                List<String> groupSet, String userData, String keyName,
                                boolean publicAddress, Object... options) throws RemoteException;

    /**
     * Stops an Amazon EC2 instance.
     * @param instanceID the Amazon EC2 instance ID to shutdown
     * @throws RemoteException if there is a network failure
     */
    void shutdownInstance(String instanceID) throws RemoteException;

    List<String> getGroupsNames() throws RemoteException;
    void createGridGroup(String gridName) throws RemoteException;
    void createProfileGroup(NodeProfile profile) throws RemoteException;

}
