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

import com.elasticgrid.storage.Container;
import com.elasticgrid.storage.Storable;
import com.elasticgrid.storage.StorageException;
import com.elasticgrid.storage.StorableNotFoundException;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link Container} providing support for Amazon S3.
 *
 * @author Jerome Bernard
 */
public class S3Container implements Container {
    private final S3Service s3;
    private final S3Bucket bucket;
    private final MimetypesFileTypeMap mimes;

    public S3Container(final S3Service s3, final S3Bucket bucket) {
        this.s3 = s3;
        this.bucket = bucket;
        this.mimes = new MimetypesFileTypeMap();
    }

    public String getName() {
        return bucket.getName();
    }

    public List<Storable> listStorables() throws StorageException {
        try {
            S3Object[] objects = s3.listObjects(bucket);
            List<Storable> storables = new ArrayList<Storable>(objects.length);
            for (S3Object object : objects) {
                storables.add(new S3Storable(s3, object));
            }
            return storables;
        } catch (S3ServiceException e) {
            throw new StorageException("Can't list storables", e);
        }
    }

    public Storable findStorableByName(String name) throws StorableNotFoundException, StorageException {
        try {
            S3Object object = s3.getObject(bucket, name);
            return new S3Storable(s3, object);
        } catch (S3ServiceException e) {
            if ("NoSuchKey".equals(e.getS3ErrorCode()))
                throw new StorableNotFoundException(name);
            else
                throw new StorageException("Can't get storable", e);
        }
    }

    public Storable uploadStorable(File file) throws StorageException {
        return uploadStorable(file.getName(), file);
    }

    public Storable uploadStorable(String key, File file) throws StorageException {
        try {
            S3Object object = new S3Object(file);
            object.setContentType(mimes.getContentType(file));
            object.setKey(key);
            return new S3Storable(s3, s3.putObject(bucket, object));
        } catch (Exception e) {
            throw new StorageException("Can't upload storable from file", e);
        }
    }

    public Storable uploadStorable(String key, InputStream stream, String mimeType) throws StorageException {
        try {
            S3Object object = new S3Object(key);
            object.setContentType(mimeType);
            object.setDataInputStream(stream);
            return new S3Storable(s3, s3.putObject(bucket, object));
        } catch (Exception e) {
            throw new StorageException("Can't upload storable from stream", e);
        }
    }

    public void deleteStorable(String name) throws StorageException {
        try {
            s3.deleteObject(bucket, name);
        } catch (Exception e) {
            throw new StorageException("Can't delete storable", e);
        }
    }
}
