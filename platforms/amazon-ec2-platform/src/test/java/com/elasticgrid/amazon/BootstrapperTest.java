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
import com.elasticgrid.utils.amazon.AWSUtils;
import com.elasticgrid.amazon.boot.Bootstrapper;

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

        Properties ec2props = AWSUtils.loadEC2Configuration();

        StringBuffer launchParameters = new StringBuffer();
        String awsAccessId = (String) ec2props.get(AWSUtils.AWS_ACCESS_ID);
        String awsSecretKey = (String) ec2props.get(AWSUtils.AWS_SECRET_KEY);

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
