/**
 * Elastic Grid
 * Copyright (C) 2008-2009 Elastic Grid, LLC.
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

package com.elasticgrid.platforms.rackspace;

import net.elasticgrid.rackspace.cloudservers.CloudServers;
import net.elasticgrid.rackspace.cloudservers.CloudServersException;
import static java.lang.String.format;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class CloudServersNodeInstantiatorImpl implements CloudServersNodeInstantiator {
    private CloudServers cs;
    private static final Logger logger = Logger.getLogger(CloudServersNodeInstantiator.class.getName());

    public List<String> startInstances(String imageID, int minCount, int maxCount, List<String> groupSet, String userData, String keyName, boolean publicAddress, Object... options) throws RemoteException {
        /*
        if (StringUtils.isEmpty(imageID))
            throw new IllegalArgumentException("imageID can't be null");
        EC2NodeType instanceType = (EC2NodeType) options[0];
        logger.log(Level.FINER, "Starting {0} Amazon EC2 instance from image ''{1}'': keyName={2}, groups={3}, userdata={4}, instanceType={5}",
                new Object[] { minCount, imageID, keyName, groupSet, userData, instanceType });
        com.xerox.amazonws.ec2.InstanceType type;
        switch (instanceType) {
            case SMALL:
                type = com.xerox.amazonws.ec2.InstanceType.DEFAULT;
                break;
            case MEDIUM_HIGH_CPU:
                type = com.xerox.amazonws.ec2.InstanceType.MEDIUM_HCPU;
                break;
            case LARGE:
                type = com.xerox.amazonws.ec2.InstanceType.LARGE;
                break;
            case EXTRA_LARGE:
                type = com.xerox.amazonws.ec2.InstanceType.XLARGE;
                break;
            case EXTRA_LARGE_HIGH_CPU:
                type = com.xerox.amazonws.ec2.InstanceType.XLARGE_HCPU;
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
                    try {
                        reservation = jec2.describeInstances(Arrays.asList(last.getInstanceId())).get(0);
                        last = reservation.getInstances().get(instances.size() - 1);
                    } catch (EC2Exception e) {
                        // do nothing -- sometimes EC2 don't allow us to request description of instance right after we start it!
                        logger.warning(java.lang.String.format("Could not get instance description for instance '%s'. Retrying...",
                                last.getInstanceId()));
                    }
                }
            } catch (InterruptedException e) {
                String message = java.lang.String.format("Couldn't start properly %d Amazon EC2 instances." +
                        "Make sure instances of reservation ID %s are started and used properly within your cluster",
                        minCount, reservation.getReservationId());
                logger.log(Level.SEVERE, message, e);
                throw new RemoteException(message, e);
            }
            List<String> instancesIDs = new ArrayList<String>(instances.size());
            for (ReservationDescription.Instance instance : instances) {
                instancesIDs.add(instance.getInstanceId());
            }
            logger.log(Level.INFO, "Started {0} Amazon EC2 instance from image ''{1}''...", new Object[] { minCount, imageID });
            return instancesIDs;
        } catch (EC2Exception e) {
            throw new RemoteException("Can't start Amazon EC2 instances", e);
        }
        */
        return Collections.emptyList();
    }

    public void shutdownInstance(String serverID) throws RemoteException {
        logger.info(format("Shutting down Rackspace Cloud Server instance '%s'...", serverID));
        try {
            cs.deleteServer(Integer.parseInt(serverID));
        } catch (CloudServersException e) {
            throw new RemoteException(format("Can't stop Rackspace Cloud Server instance '%s'", serverID), e);
        }
    }

    public List<String> getGroupsNames() throws RemoteException {
        /*
        List<GroupDescription> groups;
        try {
            groups = jec2.describeSecurityGroups(new String[] {});
        } catch (EC2Exception e) {
            if (e.getCause() instanceof UnknownHostException) {
                logger.log(Level.WARNING, "EC2: EC2 is not reachable. Ignoring cluster groups...");
                return Arrays.asList(Discovery.MONITOR.getGroupName(), Discovery.AGENT.getGroupName());
            } else {
                throw new RemoteException("Can't get list of group names", e);
            }
        }
        List<String> groupNames = new ArrayList<String>(groups.size());
        for (GroupDescription group : groups) {
            groupNames.add(group.getName());
        }
        return groupNames;
        */
        return Collections.emptyList();
    }

    /*
    public void createSecurityGroup(String group) throws RemoteException {
        try {
            jec2.createSecurityGroup(group, "Elastic Grid " + group);
        } catch (EC2Exception e) {
            throw new RemoteException("Can't create security group '" + group + "'", e);
        }
    }
    */

    public void setCloudServers(CloudServers cs) {
        this.cs = cs;
    }
}