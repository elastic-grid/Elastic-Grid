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

package com.elasticgrid.platforms.ec2.utils;

import com.elasticgrid.platforms.ec2.config.EC2Configuration;
import com.elasticgrid.utils.amazon.AWSUtils;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.security.AWSCredentials;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@link URLStreamHandler} for <a href="http://aws.amazon.com/s3">Amazon S3</a>.
 * The URL scheme for this handler is: <pre>s3://&lt;bucketname&gt;/&lt;key&gt;</pre>
 *
 * This handler has to be installed before any connection is made by using the following code:
 * <pre>URL.setURLStreamHandlerFactory(new AWSURLStreamHandlerFactory());</pre>
 *
 * @author Jerome Bernard
 */
public class S3URLStreamHandler extends URLStreamHandler {
    private static final Logger logger = Logger.getLogger(S3URLStreamHandler.class.getName());

    protected URLConnection openConnection(URL url) throws IOException {
        Properties awsConfig = AWSUtils.loadEC2Configuration();
        String awsAccessID = awsConfig.getProperty(EC2Configuration.AWS_ACCESS_ID);
        String awsSecretKey = awsConfig.getProperty(EC2Configuration.AWS_SECRET_KEY);
        if (awsAccessID == null) {
            throw new IllegalArgumentException("Could not find AWS Access ID");
        }
        if (awsSecretKey == null) {
            throw new IllegalArgumentException("Could not find AWS Secret Key");
        }
        AWSCredentials credentials = new AWSCredentials(awsAccessID, awsSecretKey);

        S3Service s3;
        try {
            s3 = new RestS3Service(credentials);
            return new S3URLConnection(url, s3);
        } catch (S3ServiceException e) {
            logger.log(Level.SEVERE, "Could not authenticate to S3", e);
            throw new IOException(e.toString());
        }
    }

}
