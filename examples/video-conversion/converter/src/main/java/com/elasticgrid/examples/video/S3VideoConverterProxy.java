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
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Proxy based on storage of videos on Amazon S3.
 * @author Jerome Bernard
 */
public class S3VideoConverterProxy extends AbstractProxy implements VideoConverter {
    private final String queueName;
    private final String bucket;
    private final String awsAccessID;
    private final String awsSecretKey;
    private final VideoConverter remote;

    public S3VideoConverterProxy(VideoConverter remote, Uuid uuid, String queueName, String bucket,
                                  String awsAccessID, String awsSecretKey) {
        super(remote, uuid);
        this.queueName = queueName;
        this.bucket = bucket;
        this.awsAccessID = awsAccessID;
        this.awsSecretKey = awsSecretKey;
        this.remote = remote;
    }

    /**
     * Upload the video from <tt>localPath</tt> to Amazon S3 bucket <tt>bucket</tt>.
     * Send an Amazon SQS message on queue <tt>queue</tt> whose content is the Amazon S3 location of the uploaded video.
     * {@inheritDoc}
     * @param localFile the path on the client machine
     * @throws RemoteException if there is a network failure
     */
    public void convert(File localFile, String format) throws VideoConversionException, RemoteException {
        Logger logger = Logger.getLogger(getClass().getName());
        logger.log(Level.INFO, "Converting video ''{0}'' to format ''{1}''...",
                new Object[] { localFile.getName(), format });
        // upload the video to Amazon S3
        String uploadedVideoURL;
        try {
            uploadedVideoURL = upload(localFile, bucket);
        } catch (FileNotFoundException e) {
            throw new VideoConversionException("Can't find video in " + localFile, e);
        } catch (S3ServiceException e) {
            throw new VideoConversionException("Can't upload video " + localFile + " into " + bucket, e);
        }
        // send a message to Amazon SQS for video conversion
        try {
            logger.log(Level.FINE, "Sending video conversion request for ''{0}''...", uploadedVideoURL);
            MessageQueue queue = SQSUtils.connectToQueue(queueName, awsAccessID, awsSecretKey);
            queue.sendMessage(uploadedVideoURL);
        } catch (SQSException e) {
            throw new VideoConversionException("Can't convert video located in " + localFile, e);
        }
    }

    public long getApproximateBacklog() throws RemoteException {
        return remote.getApproximateBacklog();
    }

    private String upload(File file, String bucketName) throws FileNotFoundException, S3ServiceException {
        S3Service s3 = new RestS3Service(new AWSCredentials(awsAccessID, awsSecretKey));
        Logger logger = Logger.getLogger(getClass().getName());
        String key = file.getName();
        logger.log(Level.FINE,
                "Uploading video ''{0}'' ({1} bytes) into bucket ''{2}'' for key ''{3}''",
                new Object[] { file, file.length(), bucketName, key });
        long start = System.currentTimeMillis();
        S3Bucket bucket = s3.createBucket(new S3Bucket(bucketName));
        S3Object object = new S3Object(bucket, file);
        object.setKey(key);
        object.setContentLength(file.length());
        object.setLastModifiedDate(new Date(file.lastModified()));
        s3.putObject(bucket, object);
        long end = System.currentTimeMillis();
        logger.log(Level.INFO,
                "Uploaded video ''{0}'' ({1} bytes) into bucket ''{2}'' for key ''{3}'' in {4}s",
                new Object[] { file, file.length(), bucketName, key, ((end - start) / 1000) });
        return key;
    }

}
