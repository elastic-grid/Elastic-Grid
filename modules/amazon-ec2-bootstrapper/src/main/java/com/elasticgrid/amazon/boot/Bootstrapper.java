/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This file is part of Elastic Grid.
 * 
 * Elastic Grid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * Elastic Grid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Elastic Grid.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elasticgrid.amazon.boot;

import com.xerox.amazonws.ec2.EC2Exception;
import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.ReservationDescription;
import com.elasticgrid.amazon.ec2.EC2GridLocator;
import com.elasticgrid.amazon.ec2.EC2GridLocatorImpl;
import com.elasticgrid.model.ec2.EC2Node;
import com.elasticgrid.model.GridException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Bootstrapper in charge of fetching the EC2 launch parameters and generating the EG configuration files.
 * The launch parameters are available in the <tt>/tmp/user-data</tt> file.
 *
 * @author Jerome Bernard
 */
public class Bootstrapper {
    public static final String LAUNCH_PARAMETER_ACCESS_ID = "AWS_ACCESS_ID";
    public static final String LAUNCH_PARAMETER_SECRET_KEY = "AWS_SECRET_KEY";
    public static final String LAUNCH_PARAMETER_SQS_SECURED = "AWS_SQS_SECURED";

    public static final String EG_PARAMETER_ACCESS_ID = "aws.accessId";
    public static final String EG_PARAMETER_SECRET_KEY = "aws.secretKey";
    public static final String EG_PARAMETER_SQS_SECURED = "aws.sqs.secured";

    public static final String EG_GROUP_MONITOR = "eg-monitor";
    public static final String EG_GROUP_AGENT = "eg-agent";

    public static final String LAUNCH_PARAMETERS_FILE = "/tmp/user-data";
    public static final String ELASTIC_GRID_CONFIGURATION_FILE = "config/eg.properties";

    private String egHome = System.getProperty("EG_HOME");

    public Bootstrapper() throws IOException, EC2Exception {
        // retreive EC2 parameters
        Properties launchParameters = fetchLaunchParameters();
        Properties egParameters = translateProperties(launchParameters);

        EC2GridLocator locator = new EC2GridLocatorImpl();
        String gridName = ""; // todo: find the current grid name
        try {
            EC2Node monitor = locator.findMonitor(gridName);
            String monitorHost = monitor.getAddress().getHostName();
            if (monitorHost.equals(InetAddress.getByName("localhost").getHostName())) {
                System.out.printf("This host is going to be the new monitor!");
            } else {
                System.out.printf("Using monitor host: %s\n", monitorHost);
                egParameters.put("eg.monitor.host", monitorHost);
            }
            FileUtils.writeStringToFile(new File(egHome + File.separator + "config", "monitor-host"), monitorHost);
        } catch (GridException e) {
            System.err.println("Could not find monitor host!");
            System.exit(-1);
        }

        // save configuration
        File file = saveConfiguration(egParameters);
        System.out.printf("Elastic Grid configuration file generated in '%s'\n", file.getAbsolutePath());
    }

    private Properties fetchLaunchParameters() throws IOException {
        Properties launchProperties = new Properties();
        InputStream launchFile = null;
        try {
            launchFile = new FileInputStream(new File(LAUNCH_PARAMETERS_FILE));
            launchProperties.load(launchFile);
            return launchProperties;
        } finally {
            IOUtils.closeQuietly(launchFile);
        }
    }

    private Properties translateProperties(Properties launchParameters) {
        // translate properties
        Properties egParameters = new Properties();
        for (Map.Entry property : launchParameters.entrySet()) {
            String key = (String) property.getKey();
            if (LAUNCH_PARAMETER_ACCESS_ID.equals(key))
                egParameters.put(EG_PARAMETER_ACCESS_ID, property.getValue());
            if (LAUNCH_PARAMETER_SECRET_KEY.equals(key))
                egParameters.put(EG_PARAMETER_SECRET_KEY, property.getValue());
            if (LAUNCH_PARAMETER_SQS_SECURED.equals(key))
                egParameters.put(EG_PARAMETER_SQS_SECURED, property.getValue());
        }
        return egParameters;
    }

    private File saveConfiguration(Properties egParameters) throws IOException {
        // write EG configuration
        if (egHome == null) {
            System.err.println("Could not find EG_HOME environment variable. Please fix this.");
            System.exit(-1);
        }
        File config = new File(egHome, ELASTIC_GRID_CONFIGURATION_FILE);
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(config);
            egParameters.store(stream, "Elastic Grid Configuration File - Generated file, please do NOT edit!");
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return config;
    }

    public static void main(String[] args) throws IOException, EC2Exception {
        System.out.printf("Preparing Elastic Grid environment...\n");
        new Bootstrapper();
    }
}
