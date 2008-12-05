/**
 * Elastic Grid
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
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
