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

package com.elasticgrid.platforms.ec2.config;

import com.elasticgrid.config.GenericConfiguration;

/**
 * Specific EC2 configuration properties.
 * @author Jerome Bernard
 */
public interface EC2Configuration extends GenericConfiguration {
    public static final String AWS_ACCESS_ID = "aws.accessId";
    public static final String AWS_SECRET_KEY = "aws.secretKey";
    public static final String AWS_EC2_SECURED = "aws.ec2.secured";
    public static final String AWS_SQS_SECURED = "aws.sqs.secured";
    public static final String AWS_EC2_KEYPAIR = "aws.ec2.keypair";
    public static final String AWS_EC2_AMI32 = "aws.ec2.ami32";
    public static final String AWS_EC2_AMI64 = "aws.ec2.ami64";

    public static final String REDHAT_YUM_PACKAGES = "redhat.yum.packages";
}
