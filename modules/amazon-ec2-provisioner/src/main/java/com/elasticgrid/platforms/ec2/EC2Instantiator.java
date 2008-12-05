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

package com.elasticgrid.platforms.ec2;

import com.elasticgrid.cluster.NodeInstantiator;
import com.elasticgrid.model.ec2.EC2Node;
import java.rmi.RemoteException;
import java.util.List;

public interface EC2Instantiator extends NodeInstantiator<EC2Node> {

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
     * @param options the type of instance to start (small, large, extra large) as a {@link InstanceType}
     * @return the IDs of the instances
     * @throws RemoteException if there is a network failure
     */
    List<String> startInstances(String imageID, int minCount, int maxCount,
                                List<String> groupSet, String userData, String keyName,
                                boolean publicAddress, Object... options) throws RemoteException;

}
