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

package com.elasticgrid.examples.video.services;

import com.elasticgrid.utils.amazon.AWSUtils;
import com.xerox.amazonws.sqs2.MessageQueue;
import com.xerox.amazonws.sqs2.SQSException;
import com.xerox.amazonws.sqs2.SQSUtils;
import java.io.IOException;
import java.util.Properties;

public class SQSVideoConversionRequester implements VideoConversionRequester {
    private String awsAccessId;
    private String awsSecretKey;

    public static final String EG_PARAMETER_ACCESS_ID = "aws.accessId";
    public static final String EG_PARAMETER_SECRET_KEY = "aws.secretKey";
    public static final String EG_PARAMETER_SQS_SECURED = "aws.sqs.secured";

    public SQSVideoConversionRequester() throws IOException {
        Properties eg = AWSUtils.loadEC2Configuration();
        System.out.println("Found " + eg.size() + " properties");
        awsAccessId = (String) eg.get(EG_PARAMETER_ACCESS_ID);
        awsSecretKey = (String) eg.get(EG_PARAMETER_SECRET_KEY);
    }

    public void convertVideo(String video, String queueName) throws SQSException {
        System.out.println("Using AccessID: " + awsAccessId + " and Secret Key: " + awsSecretKey);
        MessageQueue queue = SQSUtils.connectToQueue(queueName, awsAccessId, awsSecretKey);
        queue.sendMessage(video);
    }

}
