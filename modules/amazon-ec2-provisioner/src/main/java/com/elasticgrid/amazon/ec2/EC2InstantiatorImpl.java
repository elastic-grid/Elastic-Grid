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

import com.xerox.amazonws.ec2.EC2Exception;
import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.ReservationDescription;

import static java.lang.String.format;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EC2InstantiatorImpl implements EC2Instantiator {
    private Jec2 jec2;
    private static final Logger logger = Logger.getLogger(EC2Instantiator.class.getName());

    public List<String> startInstances(String imageID, int minCount, int maxCount, List<String> groupSet, String userData, String keyName, boolean publicAddress, InstanceType instanceType) throws RemoteException {
        logger.log(Level.INFO, "Starting {0} Amazon EC2 instance from image {1}...", new Object[] { minCount, imageID });
        logger.log(Level.FINER, "Starting {0} Amazon EC2 instance from image {1}: keyName={2}, groups={3}, userdata={4}, instanceType={5}",
                new Object[] { minCount, imageID, keyName, groupSet, userData, instanceType });
        com.xerox.amazonws.ec2.InstanceType type;
        switch (instanceType) {
            case SMALL:
                type = com.xerox.amazonws.ec2.InstanceType.DEFAULT;
                break;
            case LARGE:
                type = com.xerox.amazonws.ec2.InstanceType.LARGE;
                break;
            case EXTRA_LARGE:
                type = com.xerox.amazonws.ec2.InstanceType.XLARGE;
                break;
            default:
                throw new IllegalArgumentException(format("Invalid Amazon EC2 instance type '%s'", instanceType.getName()));
        }
        try {
            ReservationDescription reservation = jec2.runInstances(imageID, minCount, maxCount, groupSet, userData, keyName, type);
            List<ReservationDescription.Instance> instances = reservation.getInstances();
            ReservationDescription.Instance last = instances.get(instances.size() - 1);
            try {
                while (last.isPending()) {
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                String message = format("Couldn't start properly %d Amazon EC2 instances." +
                        "Make sure instances of reservation ID %s are started and used properly within your grid",
                        minCount, reservation.getReservationId());
                logger.log(Level.SEVERE, message, e);
                throw new RemoteException(message, e);
            }
            List<String> instancesIDs = new ArrayList<String>(instances.size());
            for (ReservationDescription.Instance instance : instances) {
                instancesIDs.add(instance.getInstanceId());
            }
            return instancesIDs;
        } catch (EC2Exception e) {
            throw new RemoteException("Can't start Amazon EC2 instances", e);
        }
    }

    public void shutdownInstance(String instanceID) throws RemoteException {
        logger.info(format("Shutting down Amazon EC2 instance %s...", instanceID));
    }

    public void setJec2(Jec2 jec2) {
        this.jec2 = jec2;
    }
}
