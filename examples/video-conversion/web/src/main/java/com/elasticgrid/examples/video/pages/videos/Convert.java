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

import com.elasticgrid.examples.video.services.VideoConversionRequester;
import com.elasticgrid.examples.video.pages.Index;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Convert {
    @Property
    @Persist(value = "flash")
    private String video;

    @Inject
    private VideoConversionRequester requester;

    Class onSubmitFromVideoConversionForm() throws Exception {
        System.out.println("Should convert video " + video);
        requester.convertVideo(video, "VideoConversion");
        return Index.class;
    }

}
