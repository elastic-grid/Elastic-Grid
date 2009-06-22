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

import com.elasticgrid.storage.StorageManager;
import com.elasticgrid.storage.Container;
import com.elasticgrid.storage.StorageException;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.security.AWSCredentials;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import java.util.List;
import java.util.ArrayList;

/**
 * {@link StorageManager} providing support for Amazon S3.
 *
 * @author Jerome Bernard
 */
public class S3StorageManager implements StorageManager {
    private S3Service s3;

    public S3StorageManager(String awsAccessId, String awsSecretKey) throws S3ServiceException {
        s3 = new RestS3Service(new AWSCredentials(awsAccessId, awsSecretKey));
    }

    public List<Container> getContainers() throws StorageException {
        try {
            S3Bucket[] buckets = s3.listAllBuckets();
            List<Container> containers = new ArrayList<Container>(buckets.length);
            for (S3Bucket bucket : buckets) {
                containers.add(new S3Container(s3, bucket));
            }
            return containers;
        } catch (S3ServiceException e) {
            throw new StorageException("Can't get list of containers", e);
        }
    }

    public Container createContainer(String name) throws StorageException {
        try {
            S3Bucket bucket = s3.createBucket(name);
            return new S3Container(s3, bucket);
        } catch (S3ServiceException e) {
            throw new StorageException("Can't create container", e);
        }
    }
}
