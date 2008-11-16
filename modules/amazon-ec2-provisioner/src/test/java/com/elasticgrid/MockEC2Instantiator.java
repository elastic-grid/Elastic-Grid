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

import com.elasticgrid.model.NodeProfile;
import com.elasticgrid.platforms.ec2.EC2Instantiator;
import net.jini.id.UuidFactory;
import static java.lang.String.format;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class MockEC2Instantiator implements EC2Instantiator {
    private Logger logger = Logger.getLogger(EC2Instantiator.class.getName());

    public List<String> startInstances(String imageID, int minCount, int maxCount, List<String> groupSet, String userData, String keyName, boolean publicAddress, Object... options) throws RemoteException {
        logger.info(format("Starting new Amazon EC2 instance from image %s...", imageID));
        return Collections.singletonList(UuidFactory.generate().toString());
    }

    public void shutdownInstance(String instanceID) throws RemoteException {
        logger.info(format("Shutting down Amazon EC2 instance %s...", instanceID));
    }

    public List<String> getGroupsNames() throws RemoteException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void createClusterGroup(String clusterName) throws RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void createProfileGroup(NodeProfile profile) throws RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}