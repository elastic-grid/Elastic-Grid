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
package com.elasticgrid.utils.rackspace;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RackspaceUtils {
    private static final Logger logger = Logger.getLogger(RackspaceUtils.class.getName());

    public static boolean isEnvironmentProperlySet() {
        return new File(System.getProperty("user.home") + File.separatorChar + ".eg", "eg.properties").exists()
                || new File(System.getenv("EG_HOME") + File.separatorChar + "config", "eg.properties").exists();
    }

    public static Properties loadRackspaceConfiguration() throws IOException {
        // try to load properties from $HOME/.eg/aws.properties
        Properties mossoProperties = new Properties();
        File awsPropertiesFile = new File(System.getProperty("user.home") + File.separatorChar + ".eg",
                "eg.properties");
        InputStream stream = null;
        try {
            stream = new FileInputStream(awsPropertiesFile);
            mossoProperties.load(stream);
        } catch (Exception e) {
            // do nothing -- this is expected behaviour
        } finally {
            try {
                if (stream != null)
                    stream.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Could not close stream", e);
            }
        }
        // try to load properties from $EG_HOME/config/eg.properties
        if (mossoProperties.size() == 0) {
            mossoProperties = new Properties();
            awsPropertiesFile = new File(System.getenv("EG_HOME") + File.separatorChar + "config",
                    "eg.properties");
            try {
                stream = new FileInputStream(awsPropertiesFile);
                mossoProperties.load(stream);
            } catch (Exception e) {
                // do nothing -- this is expected behaviour
            } finally {
                try {
                    if (stream != null)
                        stream.close();
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Could not close stream", e);
                }
            }
        }

        return mossoProperties;
    }

    public static String getUsername() throws IOException {
        return loadRackspaceConfiguration().getProperty("rackspace.username");
    }

    public static String getApiKey() throws IOException {
        return loadRackspaceConfiguration().getProperty("rackspace.apiKey");
    }

}