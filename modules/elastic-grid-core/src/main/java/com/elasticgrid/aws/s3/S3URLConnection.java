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

package com.elasticgrid.aws.s3;

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
    private static final Logger logger = Logger.getLogger(Handler.class.getName());

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
