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

import com.elasticgrid.amazon.ec2.EC2Instantiator;
import com.elasticgrid.model.NodeProfile;
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

    public void createGridGroup(String gridName) throws RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void createProfileGroup(NodeProfile profile) throws RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}