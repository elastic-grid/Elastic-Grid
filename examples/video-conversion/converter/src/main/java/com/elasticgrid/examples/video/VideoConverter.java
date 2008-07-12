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

import java.rmi.RemoteException;
import java.rmi.Remote;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Video Conversion service.
 * @author Jerome Bernard
 */
public interface VideoConverter extends Remote {

    /**
     * Convert a video.
     * This process is asynchronous and will return before the video is converted.
     * @param localFile the original video for which the variants should be produced
     * @param format the format to which the video should be converted (for example <tt>flv</tt>)
     * @throws VideoConversionException if the video can't be converted properly
     * @throws TimeoutException
     * @throws InterruptedException
     * @throws RemoteException if there is a network failure
     */
    void convert(File localFile, String format)
            throws VideoConversionException, TimeoutException, InterruptedException, RemoteException;

    /**
     * Retreive an approximate number of videos still to be processed.
     * @return the number of videos which are not yet converted
     * @throws RemoteException if there is a network failure
     */
    long getApproximateBacklog() throws RemoteException;

}
