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

package com.elasticgrid.utils.amazon;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AWSUtils {
    public static final String AWS_ACCESS_ID = "aws.accessId";
    public static final String AWS_SECRET_KEY = "aws.secretKey";
    private static final Logger logger = Logger.getLogger(AWSUtils.class.getName());

    public static Properties loadEC2Configuration() throws IOException {
        // try to load properties from $HOME/.eg/aws.properties
        Properties awsProperties = new Properties();
        File awsPropertiesFile = new File(System.getProperty("user.home") + File.separatorChar + ".eg",
                "aws.properties");
        InputStream stream = null;
        try {
            stream = new FileInputStream(awsPropertiesFile);
            awsProperties.load(stream);
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
        // try to load properties from $EG_HOME/eg.properties
        if (awsProperties.size() == 0) {
            awsProperties = new Properties();
            awsPropertiesFile = new File(System.getProperty("EG_HOME") + File.separatorChar + "config",
                    "eg.properties");
            try {
                stream = new FileInputStream(awsPropertiesFile);
                awsProperties.load(stream);
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

        return awsProperties;
    }

}
