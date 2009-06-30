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

package com.elasticgrid.storage.amazon.s3;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class HandlerTest {

    @BeforeClass
    public void setupURLStreamHandlerFactory() {
        URL.setURLStreamHandlerFactory(new AWSURLStreamHandlerFactory());
    }

    @Test
    public void testPrivateURL() throws IOException {
        URL u = new URL("s3://elastic-grid-distributions/elastic-grid-0.9.0-i386.manifest.xml");
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
