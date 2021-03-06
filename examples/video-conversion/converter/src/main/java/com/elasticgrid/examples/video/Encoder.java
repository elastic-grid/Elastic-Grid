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

package com.elasticgrid.examples.video;

import java.io.File;

/**
 * Encoder for videos.
 * @author Jerome Bernard
 */
public interface Encoder {
    /**
     * Convert a video to a specified codec.
     * @param original the file to convert
     * @param destName the filename for the converted video
     * @param format the video format (such as <tt>flv</tt>)
     * @param width width of the video to produce
     * @param height height of the video to produce
     * @param start where the cut should start; if null there is no cut
     * @param end where the cut should end; if null there is not cut
     * @param vbr video bit rate in kbps
     * @param abr audio bit rate in kbps
     * @param fps frames per second (usually 25 or 15)
     * @return the converted video file
     * @throws VideoConversionException if the video can't be converted properly
     * @throws InterruptedException if the video conversion is interrupted
     */
    File convertVideo(File original, String destName, String format, int width, int height,
                         Integer start, Integer end, int vbr, int abr, int fps)
            throws VideoConversionException, InterruptedException;
}
