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
