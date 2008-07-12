package com.elasticgrid.amazon;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;

import com.xerox.amazonws.ec2.EC2Exception;
import com.elasticgrid.utils.amazon.Utils;

/**
 * Test case for Bootstrapper.
 */
public class BootstrapperTest {

    @Test(groups = "qa")
    public void testBootstrap() throws IOException, EC2Exception {
        String egHome = System.getProperty("java.io.tmpdir");
        System.setProperty("EG_HOME", egHome);
        File egConfigDir = new File(egHome, "config");
        egConfigDir.mkdir();

        Properties ec2props = Utils.loadEC2Configuration();

        StringBuffer launchParameters = new StringBuffer();
        String awsAccessId = (String) ec2props.get("aws.accessId");
        String awsSecretKey = (String) ec2props.get("aws.secretKey");

        launchParameters.append(Bootstrapper.LAUNCH_PARAMETER_ACCESS_ID).append('=').append(awsAccessId).append('\n');
        launchParameters.append(Bootstrapper.LAUNCH_PARAMETER_SECRET_KEY).append('=').append(awsSecretKey).append('\n');
        launchParameters.append(Bootstrapper.LAUNCH_PARAMETER_SQS_SECURED).append('=').append("true").append('\n');
        File launchParametersFile = new File(Bootstrapper.LAUNCH_PARAMETERS_FILE);
        FileUtils.writeStringToFile(launchParametersFile, launchParameters.toString());

        new Bootstrapper();

        Properties egConfiguration = new Properties();
        InputStream stream = new FileInputStream(new File(egHome, Bootstrapper.ELASTIC_GRID_CONFIGURATION_FILE));
        egConfiguration.load(stream);
        assert awsAccessId.equals(egConfiguration.get(Bootstrapper.EG_PARAMETER_ACCESS_ID));
        assert awsSecretKey.equals(egConfiguration.get(Bootstrapper.EG_PARAMETER_SECRET_KEY));
        assert "true".equals(egConfiguration.get(Bootstrapper.EG_PARAMETER_SQS_SECURED));
        IOUtils.closeQuietly(stream);
    }

}
