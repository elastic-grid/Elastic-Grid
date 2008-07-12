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
