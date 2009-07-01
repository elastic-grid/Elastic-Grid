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

import com.elasticgrid.config.EC2Configuration;
import com.elasticgrid.storage.StorageException;
import com.elasticgrid.storage.spi.AbstractStorageEngineTest;
import com.elasticgrid.storage.spi.StorageEngine;
import com.elasticgrid.utils.amazon.AWSUtils;
import java.util.Properties;

/**
 * {@link S3StorageEngine} tests.
 *
 * @author Jerome Bernard
 */
public class S3StorageEngineTest extends AbstractStorageEngineTest {
    protected StorageEngine getStorageEngine() throws StorageException {
        try {
            Properties awsConfig = AWSUtils.loadEC2Configuration();
            String awsAccessID = awsConfig.getProperty(EC2Configuration.AWS_ACCESS_ID);
            String awsSecretKey = awsConfig.getProperty(EC2Configuration.AWS_SECRET_KEY);
            if (awsAccessID == null) {
                throw new IllegalArgumentException("Could not find AWS Access ID");
            }
            if (awsSecretKey == null) {
                throw new IllegalArgumentException("Could not find AWS Secret Key");
            }
            return new S3StorageEngine(awsAccessID, awsSecretKey);
        } catch (Exception e) {
            throw new StorageException("Can't initialize S3 Storage Manager", e);
        }
    }
}
