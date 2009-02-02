package com.elasticgrid.platforms.ec2.utils;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class S3URLStreamHandlerTest {

    @BeforeClass
    public void setupURLStreamHandlerFactory() {
        URL.setURLStreamHandlerFactory(new AWSURLStreamHandlerFactory());
    }

    @Test
    public void testPrivateURL() throws IOException {
        URL u = new URL("s3://elastic-grid-applications/fps-console.war");
        URLConnection connect = u.openConnection();
        // Write download to stdout.
        final int bufferlength = 4096;
        byte[] buffer = new byte[bufferlength];
        InputStream is = connect.getInputStream();
        try {
            for (int count = -1;
                 (count = is.read(buffer, 0, bufferlength)) != -1;) {
                System.out.write(buffer, 0, count);
            }
            System.out.flush();
        } finally {
            is.close();
        }
    }
}
