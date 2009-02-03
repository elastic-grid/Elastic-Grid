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

package com.elasticgrid.examples.video.pages.videos;

import com.elasticgrid.examples.video.VideoConverter;
import com.elasticgrid.examples.video.pages.Index;
import com.elasticgrid.examples.video.util.ServiceLocator;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.upload.services.UploadedFile;

import java.io.File;
import java.rmi.RemoteException;

public class ConvertAndUpload {
    @Property
    @Persist(value = "flash")
    private UploadedFile video;

    @Property
    @Persist(value = "flash")
    private String format = "flv";

    private VideoConverter converter;

    Class onSubmitFromVideoConversionForm() throws Exception {
        System.out.println("Should convert video " + video);
        File copied = File.createTempFile(System.getProperty("java.io.tmpdir"), video.getFileName());
        video.write(copied);
        converter.convert(copied, format);
        return Index.class;
    }

    void onActivate() throws RemoteException, InterruptedException {
        converter = (VideoConverter) ServiceLocator.getServicesByClass(VideoConverter.class)[0].service;
    }

}