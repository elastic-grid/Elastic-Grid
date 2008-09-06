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

package com.elasticgrid.examples.video;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Encoder using mencoder.
 * @author Jerome Bernard
 */
public class MencoderEncoder implements Encoder {
    private String encoderLocation;
    private boolean enableLog;
    private static final Logger logger = Logger.getLogger(MencoderEncoder.class.getName());

    public MencoderEncoder(boolean enableLog) {
        this.enableLog = enableLog;
        String rioHome = System.getProperty("org.rioproject.home");
        if (rioHome == null) {
            throw new NullPointerException("Rio home property should be set!");
        }
        String osName = System.getProperty("os.name");
        try {
            if (osName.contains("Windows")) {
                encoderLocation = "mencoder.exe";
            } else if (osName.contains("Linux")) {
                encoderLocation = "mencoder";
            } else if (osName.contains("Mac")) {
                encoderLocation = "mencoder";
            } else {
                throw new RuntimeException("Unsupported OS: " + osName);
            }
            logger.log(Level.INFO, "Using encoder located in {0}", encoderLocation);
        } catch (Exception e) {
            throw new RuntimeException("Can't find appropriate mencoder software", e);
        }
    }

    public File convertVideo(File original, String destName, String format, int width, int height,
                         Integer start, Integer end, int vbr, int abr, int fps) throws VideoConversionException, InterruptedException {
        File videoConverted = new File(original.getParent(), destName);

        logger.log(Level.FINE, "Converting video {0} into {1} as {2} format...", new Object[] { original, videoConverted, format });

        String command = String.format(
                "%s %s -ofps %d -of lavf"
                + " -ovc lavc -lavcopts vcodec=%s:vbitrate=%d:mbd=2:mv0:trell:v4mv:cbp:last_pred=3 -vf scale=%d:%d"
                + " -oac mp3lame -lameopts cbr:br=%d -srate 22050 -o %s",
                encoderLocation, original.getAbsolutePath(), fps, format, vbr, width, height, abr, videoConverted.getAbsolutePath());
        if (start != null && end != null)
            command = String.format("%s -ss %d -endpos %d", command, start, end);

        // run the external conversion program
        File log = new File(videoConverted.getParent(), videoConverted.getName().replace(format, "log"));
        FileWriter fileWriter = null;
        try {
            fileWriter = enableLog ? new FileWriter(log) : null;
            logger.log(Level.FINEST, "Created log file in {0}", log);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Can't open log file. Skipping...", e);
            fileWriter = null;
        }
        try {
            logger.finest(command);
            final Writer logWriter = enableLog ? new BufferedWriter(fileWriter) : null;
            final Process process = Runtime.getRuntime().exec(command);
            new Thread(new Runnable() {
                public void run() {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line;
                        try {
                            while ((line = reader.readLine()) != null) {
                                if (enableLog)
                                    IOUtils.write(line, logWriter);
                            }
                        } finally {
                            reader.close();
                        }
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }).start();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                        String line;
                        try {
                            while ((line = reader.readLine()) != null) {
                                if (enableLog)
                                    IOUtils.write(line, logWriter);
                            }
                        } finally {
                            reader.close();
                        }
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }).start();
            process.waitFor();
            return videoConverted;
        } catch (IOException e) {
            throw new VideoConversionException("Can't run video conversion software", e);
        } finally {
            if (enableLog)
                IOUtils.closeQuietly(fileWriter);
        }
    }
}
