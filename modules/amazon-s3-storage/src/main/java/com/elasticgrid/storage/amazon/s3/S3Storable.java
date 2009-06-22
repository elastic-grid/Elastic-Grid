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

import com.elasticgrid.storage.Storable;
import org.jets3t.service.S3Service;
import org.jets3t.service.model.S3Object;

/**
 * {@link Storable} providing support for Amazon S3.
 *
 * @author Jerome Bernard
 */
public class S3Storable implements Storable {
    private final S3Service s3;
    private final S3Object object;

    public S3Storable(final S3Service s3, final S3Object object) {
        this.s3 = s3;
        this.object = object;
    }

    public String getName() {
        return object.getKey();
    }
}
