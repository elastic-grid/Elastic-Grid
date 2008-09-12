/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This file is part of Elastic Grid.
 * 
 * Elastic Grid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * 
 * Elastic Grid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Elastic Grid.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elasticgrid.examples.video;

import com.xerox.amazonws.sqs2.MessageQueue;
import com.xerox.amazonws.sqs2.SQSException;
import com.xerox.amazonws.sqs2.SQSUtils;
import net.jini.id.Uuid;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.rioproject.resources.servicecore.AbstractProxy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Proxy based on local storage of videos.
 * @author Jerome Bernard
 */
public class LocalVideoConverterProxy extends AbstractProxy implements VideoConverter {
    private final String queueName;
    private final String awsAccessID;
    private final String awsSecretKey;
    private VideoConverter remote;

    public LocalVideoConverterProxy(VideoConverter remote, Uuid uuid, String queueName,
                                  String awsAccessID, String awsSecretKey) {
        super(remote, uuid);
        this.queueName = queueName;
        this.awsAccessID = awsAccessID;
        this.awsSecretKey = awsSecretKey;
        this.remote = remote;
    }

    /**
     * Upload the video from <tt>localPath</tt> to Amazon S3 bucket <tt>bucket</tt>.
     * Send an Amazon SQS message on queue <tt>queue</tt> whose content is the Amazon S3 location of the uploaded video.
     * {@inheritDoc}
     * @param localFile the path on the client machine
     * @throws java.rmi.RemoteException if there is a network failure
     */
    public void convert(File localFile, String format) throws VideoConversionException, RemoteException {
        Logger logger = Logger.getLogger(getClass().getName());
        logger.log(Level.INFO, "Converting video ''{0}'' to format ''{1}''...",
                new Object[] { localFile.getName(), format });
        // send a message to Amazon SQS for video conversion
        try {
            URL videoUrl = localFile.toURL();
            logger.log(Level.FINE, "Sending video conversion request for ''{0}''...", videoUrl);
            MessageQueue queue = SQSUtils.connectToQueue(queueName, awsAccessID, awsSecretKey);
            queue.sendMessage(videoUrl.toString());
        } catch (SQSException e) {
            throw new VideoConversionException("Can't convert video located in " + localFile, e);
        } catch (MalformedURLException e) {
            throw new VideoConversionException("Can't create URL for video " + localFile, e);
        }
    }

    public long getApproximateBacklog() throws RemoteException {
        return remote.getApproximateBacklog();
    }

}