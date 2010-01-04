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

import com.elasticgrid.platforms.ec2.EC2NodeInstantiator;
import net.jini.id.UuidFactory;
import static java.lang.String.format;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class MockEC2NodeInstantiator implements EC2NodeInstantiator {
    private Logger logger = Logger.getLogger(EC2NodeInstantiator.class.getName());

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

    public void createSecurityGroup(String group) throws RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
