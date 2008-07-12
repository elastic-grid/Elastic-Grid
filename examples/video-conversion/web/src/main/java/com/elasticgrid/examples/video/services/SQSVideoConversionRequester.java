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

package com.elasticgrid.examples.video.services;

import com.xerox.amazonws.sqs2.MessageQueue;
import com.xerox.amazonws.sqs2.SQSUtils;
import com.xerox.amazonws.sqs2.SQSException;

import java.util.Properties;
import java.util.Map;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;

import org.apache.commons.io.IOUtils;

public class SQSVideoConversionRequester implements VideoConversionRequester {
    private String awsAccessId;
    private String awsSecretKey;

    public static final String LAUNCH_PARAMETER_ACCESS_ID = "AWS_ACCESS_ID";
    public static final String LAUNCH_PARAMETER_SECRET_KEY = "AWS_SECRET_KEY";
    public static final String LAUNCH_PARAMETER_SQS_SECURED = "AWS_SQS_SECURED";

    public static final String EG_PARAMETER_ACCESS_ID = "aws.accessId";
    public static final String EG_PARAMETER_SECRET_KEY = "aws.secretKey";
    public static final String EG_PARAMETER_SQS_SECURED = "aws.sqs.secured";

    public SQSVideoConversionRequester() throws IOException {
        Properties ec2 = fetchLaunchParameters();
        Properties eg = translateProperties(ec2);
        awsAccessId = (String) eg.get(EG_PARAMETER_ACCESS_ID);
        awsSecretKey = (String) eg.get(EG_PARAMETER_SECRET_KEY);
    }

    public void convertVideo(String video, String queueName) throws SQSException {
        MessageQueue queue = SQSUtils.connectToQueue(queueName, awsAccessId, awsSecretKey);
        queue.sendMessage(video);
    }

    private Properties fetchLaunchParameters() throws IOException {
        Properties launchProperties = new Properties();
        InputStream launchFile = null;
        try {
            launchFile = new FileInputStream(new File("/tmp/user-data"));
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

}
