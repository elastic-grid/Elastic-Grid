package com.elasticgrid.platforms.ec2.utils;

import java.net.URLStreamHandlerFactory;
import java.net.URLStreamHandler;

/**
 * {@link URLStreamHandlerFactory} for <a href="http://aws.amazon.com">Amazon Web Services</a>.
 *
 * This handler has to be installed before any connection is made by using the following code:
 * <pre>URL.setURLStreamHandlerFactory(new AWSURLStreamHandlerFactory());</pre>
 *
 * @author Jerome Bernard
 */
public class AWSURLStreamHandlerFactory implements URLStreamHandlerFactory {
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (protocol.equalsIgnoreCase("s3")) {
            return new S3URLStreamHandler();
        } else {
            return null;
        }
    }
}
