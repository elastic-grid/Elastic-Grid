package com.elasticgrid.platforms.ec2.utils;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@link URLConnection} for <a href="http://aws.amazon.com/s3">Amazon S3</a>.
 *
 * @author Jerome Bernard
 */
public class S3URLConnection extends URLConnection {
    private S3Service s3;
    private static final Logger logger = Logger.getLogger(S3URLStreamHandler.class.getName());

    public S3URLConnection(URL url, S3Service s3) {
        super(url);
        this.s3 = s3;
    }

    public void connect() throws IOException {
    }

    @Override
    public InputStream getInputStream() throws IOException {
        try {
            // This opens the stream to the bucket/key object.
            S3Object s3obj = s3.getObject(new S3Bucket(url.getHost()), url.getPath().substring(1));
            return s3obj.getDataInputStream();
        } catch (S3ServiceException e) {
            logger.log(Level.SEVERE, "Could not get object from S3", e);
            throw new IOException(e.toString());
        }
    }
}
