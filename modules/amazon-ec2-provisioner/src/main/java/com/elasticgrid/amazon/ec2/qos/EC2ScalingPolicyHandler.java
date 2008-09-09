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

package com.elasticgrid.amazon.ec2.qos;

import com.elasticgrid.amazon.ec2.EC2Instantiator;
import com.elasticgrid.amazon.ec2.FakeEC2Instantiator;
import com.elasticgrid.grid.ec2.InstanceType;
import org.rioproject.core.jsb.ServiceBeanContext;
import org.rioproject.event.EventHandler;
import org.rioproject.sla.SLA;
import org.rioproject.sla.ScalingPolicyHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import static java.lang.String.format;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EC2ScalingPolicyHandler extends ScalingPolicyHandler {
    private EC2Instantiator ec2;
    private String amazonImageID;
    private String keyName;
    private List<String> groups;
    private Boolean publicAddress;
    private static final String AMAZON_INSTANCE_ID_PARAMETER = "amazonInstanceID";
    private static final Logger logger = Logger.getLogger(EC2ScalingPolicyHandler.class.getName());

    public EC2ScalingPolicyHandler(SLA sla) {
        super(sla);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(Object eventSource, EventHandler eventHandler, ServiceBeanContext context) {
        super.initialize(eventSource, eventHandler, context);
        try {
            amazonImageID = (String) context.getConfiguration().getEntry("com.elasticgrid.amazon.ec2",
                    "amazonImageID", String.class);
            keyName = (String) context.getConfiguration().getEntry("com.elasticgrid.amazon.ec2",
                    "keyName", String.class);
            groups = (List<String>) context.getConfiguration().getEntry("com.elasticgrid.amazon.ec2",
                    "groups", List.class, Collections.emptyList());
            publicAddress = (Boolean) context.getConfiguration().getEntry("com.elasticgrid.amazon.ec2",
                    "publicAddress", Boolean.class, Boolean.TRUE);
            ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{
                    "/com/elasticgrid/amazon/ec2/applicationContext.xml",
            });
            ec2 = (EC2Instantiator) ctx.getBean("ec2", EC2Instantiator.class);
            if (ec2 == null) {
                logger.log(Level.WARNING, "No EC2 instantiator specified. Using {0} implementation.",
                        FakeEC2Instantiator.class.getName());
                ec2 = new FakeEC2Instantiator();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doIncrement() {
        try {
            // todo: do not fake the user data!
            String userdata = "GRID_NAME=test,AWS_ACCESS_ID=123456,AWS_SECRET_KEY=123456,AWS_SQS_SECURED=true";
            List<String> instances = ec2.startInstances(amazonImageID, 1, 1,
                    groups, userdata, keyName, publicAddress, InstanceType.SMALL);
            String instanceID = instances.get(0);
            logger.log(Level.INFO, "Started Amazon EC2 instance {0}", instanceID);
            context.getServiceBeanConfig().getInitParameters().put(AMAZON_INSTANCE_ID_PARAMETER, instanceID);
        } catch (RemoteException e) {
            logger.log(Level.SEVERE, format("Can't start EC2 instance from AMI %s", amazonImageID), e);
        }
        super.doIncrement();
    }

    @Override
    protected void doDecrement() {
        super.doDecrement();
        String instanceID = (String) context.getServiceBeanConfig().getInitParameters().get(AMAZON_INSTANCE_ID_PARAMETER);
        try {
            ec2.shutdownInstance(instanceID);
            logger.log(Level.INFO, "Stopped Amazon EC2 instance {0}", instanceID);
        } catch (RemoteException e) {
            logger.log(Level.SEVERE, format("Can't shutdown EC2 instance %s", instanceID), e);
        }
    }

    @Override
    public String getDescription() {
        return "EC2 Scaling Policy Handler";
    }
}
