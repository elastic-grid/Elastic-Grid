/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * Licensed under the GNU Lesser General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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