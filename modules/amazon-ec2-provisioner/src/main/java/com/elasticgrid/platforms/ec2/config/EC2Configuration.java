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
