/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This file is part of Elastic Grid.
 * 
 * Elastic Grid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * Elastic Grid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Elastic Grid.  If not, see <http://www.gnu.org/licenses/>.
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
