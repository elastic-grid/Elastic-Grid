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
package com.elasticgrid.platforms.ec2;

import com.elasticgrid.cluster.spi.CloudPlatformManagerFactory;
import com.elasticgrid.config.EC2Configuration;
import com.elasticgrid.model.ec2.EC2Cluster;
import com.elasticgrid.platforms.ec2.discovery.EC2SecurityGroupsClusterLocator;
import com.elasticgrid.utils.amazon.AWSUtils;
import com.xerox.amazonws.ec2.Jec2;
import java.io.IOException;
import java.util.Properties;

public class EC2CloudPlatformManagerFactory implements CloudPlatformManagerFactory<EC2Cluster> {
    public EC2CloudPlatformManager getInstance() throws IOException {
        Properties config = AWSUtils.loadEC2Configuration();

        String awsAccessId = config.getProperty(EC2Configuration.AWS_ACCESS_ID);
        String awsSecretKey = config.getProperty(EC2Configuration.AWS_SECRET_KEY);
        boolean secured = Boolean.parseBoolean(config.getProperty(EC2Configuration.AWS_EC2_SECURED));

        Jec2 ec2 = new Jec2(awsAccessId, awsSecretKey, secured);

        EC2SecurityGroupsClusterLocator ec2ClusterLocator = new EC2SecurityGroupsClusterLocator();
        ec2ClusterLocator.setEc2(ec2);
        EC2InstantiatorImpl ec2Instantiator = new EC2InstantiatorImpl();
        ec2Instantiator.setEc2(ec2);

        EC2CloudPlatformManager ec2CloudPlatformManager = new EC2CloudPlatformManager();
        ec2CloudPlatformManager.setOverridesBucket(config.getProperty(EC2Configuration.EG_DROP_BUCKET));
        ec2CloudPlatformManager.setAwsAccessID(awsAccessId);
        ec2CloudPlatformManager.setAwsSecretKey(awsSecretKey);
        ec2CloudPlatformManager.setAwsSecured(secured);
        ec2CloudPlatformManager.setAmi32(config.getProperty(EC2Configuration.AWS_EC2_AMI32));
        ec2CloudPlatformManager.setAmi64(config.getProperty(EC2Configuration.AWS_EC2_AMI32));
        ec2CloudPlatformManager.setClusterLocator(ec2ClusterLocator);
        ec2CloudPlatformManager.setNodeInstantiator(ec2Instantiator);

        return ec2CloudPlatformManager;
    }
}
