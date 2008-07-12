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

import com.elasticgrid.amazon.ec2.EC2Instantiator;
import com.elasticgrid.amazon.ec2.FakeEC2Instantiator;
import com.elasticgrid.amazon.ec2.InstanceType;
import org.rioproject.core.jsb.ServiceBeanContext;
import org.rioproject.event.DynamicEventConsumer;
import org.rioproject.event.EventDescriptor;
import org.rioproject.event.RemoteServiceEvent;
import org.rioproject.event.RemoteServiceEventListener;
import org.rioproject.jsb.ServiceBeanAdapter;
import org.rioproject.monitor.ProvisionFailureEvent;

import static java.lang.String.format;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EC2ProvisionMonitorImpl extends ServiceBeanAdapter implements RemoteServiceEventListener, EC2ProvisionMonitor {
    private EC2Instantiator instantiator;
    private DynamicEventConsumer provisionFailuresEventConsumer;
    private String amazonImageID;
    private String keyName;
    private Boolean secured;
    private Boolean publicAddress;
    private List<String> securityGroups;
    private Logger logger = Logger.getLogger(EC2ProvisionMonitor.class.getName());

    @Override @SuppressWarnings("unchecked")
    public void initialize(ServiceBeanContext context) throws Exception {
        super.initialize(context);
        amazonImageID = (String) context.getConfiguration().getEntry(
                "com.elasticgrid.amazon.ec2", "amazonImageID",
                String.class);
        keyName = (String) context.getConfiguration().getEntry(
                "com.elasticgrid.amazon.ec2", "keyName",
                String.class);
        secured = (Boolean) context.getConfiguration().getEntry(
                "com.elasticgrid.amazon.ec2", "secured",
                Boolean.class, Boolean.TRUE);
        publicAddress = (Boolean) context.getConfiguration().getEntry(
                "com.elasticgrid.amazon.ec2", "publicAddress",
                Boolean.class, Boolean.TRUE);
//        securityGroups = (List<String>) context.getConfiguration().getEntry(
//                "com.elasticgrid.amazon.ec2", "securityGroups",
//                List.class);

        instantiator = new FakeEC2Instantiator();

        provisionFailuresEventConsumer = new DynamicEventConsumer(
                new EventDescriptor(ProvisionFailureEvent.class, ProvisionFailureEvent.ID),
                this, context.getDiscoveryManagement());
    }

    public void notify(RemoteServiceEvent evt) {
        if (evt instanceof ProvisionFailureEvent) {
            ProvisionFailureEvent event = (ProvisionFailureEvent) evt;
            logger.info(format("Received ProvisionFailureEvent seqno=%d, reason=%s, service element=%s",
                    evt.getSequenceNumber(), event.getReason(), event.getServiceElement()));
            try {
                startInstance(amazonImageID);
            } catch (RemoteException e) {
                logger.log(Level.SEVERE, "Unexpected error", e);
            }
        } else {
            logger.warning("Unexpected event received: " + evt);
        }
    }

    public String startInstance(String imageID) throws RemoteException {
        List<String> instances = instantiator.startInstances(imageID, 1, 1, securityGroups, null, keyName, true, InstanceType.SMALL);
        return instances.get(0);
    }

    public void shutdownInstance(String instanceID) throws RemoteException {
        instantiator.shutdownInstance(instanceID);
    }

    @Override
    public void destroy() {
        if (provisionFailuresEventConsumer != null) {
            provisionFailuresEventConsumer.deregister(this);
            provisionFailuresEventConsumer.terminate();
        }
        super.destroy();
    }
    
}