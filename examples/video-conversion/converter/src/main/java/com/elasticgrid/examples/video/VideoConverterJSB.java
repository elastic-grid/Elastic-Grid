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

import com.elasticgrid.amazon.sqs.SQSEvent;
import com.elasticgrid.amazon.sqs.SQSListener;
import com.elasticgrid.amazon.sqs.SQSServiceBeanAdapter;
import com.elasticgrid.amazon.sqs.SQSUtils;
import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import org.apache.commons.io.IOUtils;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.multithread.S3ServiceSimpleMulti;
import org.jets3t.service.security.AWSCredentials;
import org.rioproject.core.jsb.ServiceBeanContext;
import org.rioproject.watch.StopWatch;

import java.io.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.URL;

/**
 * Worker processing Amazon SQS requests for video conversions.
 * @author Jerome Bernard
 */
public class VideoConverterJSB extends SQSServiceBeanAdapter implements VideoConverter, SQSListener {
    private S3Service s3;
    private S3ServiceSimpleMulti multi;
    private ExecutorService pool;
    private String srcBucketName, destBucketName;
    private String temporaryStorageLocation;
    private Encoder encoder;
    private boolean enableS3 = false;
    private File statisticsFile = null;
    private boolean enableStatistics = false;
    private StopWatch totalProcessingWatch = new StopWatch("Total processing timing");
    private StopWatch downloadWatch = new StopWatch("Download timing");
    private StopWatch conversionWatch = new StopWatch("Conversion timing");
    private StopWatch uploadWatch = new StopWatch("Upload timing");
    private static final int NUMBER_OF_VIDEO_VARIANTS = 4;
    private static final Logger logger = Logger.getLogger(VideoConverterJSB.class.getName());

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(ServiceBeanContext context) throws Exception {
        super.initialize(context);
        // fall-back to JSB configuration
        Configuration config = context.getConfiguration();
        Encoder encoder = (Encoder) config.getEntry(getClass().getPackage().getName() + ".VideoConverter",
                "encoder", Encoder.class);
        String destBucketName = (String) config.getEntry(getClass().getPackage().getName() + ".VideoConverter",
                "encodedVideosBucket", String.class);
        String temporaryStorageLocation = (String) config.getEntry(getClass().getPackage().getName() + ".VideoConverter",
                "temporaryStorageLocation", String.class, System.getProperty("java.io.tmpdir"));
        statisticsFile = (File) config.getEntry(getClass().getPackage().getName() + ".VideoConverter",
                "statistics", File.class, null);
        enableStatistics = statisticsFile != null;
        AWSCredentials credentials = new AWSCredentials(awsAccessId, awsSecretKey);
        initialize(encoder, new RestS3Service(credentials), srcBucketName, destBucketName, temporaryStorageLocation);
        // setup watches
        context.getWatchRegistry().register(totalProcessingWatch, downloadWatch, conversionWatch, uploadWatch);
    }

    protected Object createProxy() {
        super.createProxy();
        try {
            Configuration config = context.getConfiguration();
            enableS3 = (Boolean) config.getEntry(getClass().getPackage().getName() + ".VideoConverter",
                    "enableS3", Boolean.class, false);
            srcBucketName = (String) config.getEntry(getClass().getPackage().getName() + ".VideoConverter",
                "sourceVideosBucket", String.class);
            if (enableS3) {
                return new S3VideoConverterProxy((VideoConverter) getExportedProxy(), getUuid(),
                        SQSUtils.getQueueName(queue), srcBucketName, awsAccessId, awsSecretKey);
            } else {
                return new LocalVideoConverterProxy((VideoConverter) getExportedProxy(), getUuid(),
                        SQSUtils.getQueueName(queue), awsAccessId, awsSecretKey);
            }
        } catch (ConfigurationException e) {
            throw new RuntimeException("Can't figure out if Amazon S3 should be used for video storage...", e);
        }
    }

    void initialize(Encoder encoder, S3Service s3, String srcBucketName, String destBucketName, String temporaryStorageLocation) {
        if (encoder == null)
            throw new IllegalArgumentException("encoder should not be null!");
        this.encoder = encoder;
        if (s3 == null) {
            throw new IllegalArgumentException("s3 should not be null!");
        }
        this.s3 = s3;
        this.multi = new S3ServiceSimpleMulti(s3);
        this.pool = Executors.newFixedThreadPool(NUMBER_OF_VIDEO_VARIANTS + 2);
        this.srcBucketName = srcBucketName;
        this.destBucketName = destBucketName;
        this.temporaryStorageLocation = temporaryStorageLocation;
    }

    @Override
    public void destroy(boolean force) {
        super.destroy(force);
        if (pool != null) {
            if (force)
                pool.shutdownNow();
            else
                pool.shutdown();
        }
    }

    public void handle(SQSEvent event) {
        totalProcessingWatch.startTiming();
        long start = System.currentTimeMillis();
        long videoFileSize;
        try {
            if (enableS3)
                videoFileSize = handleFromS3(event);
            else
                videoFileSize = handleFromLocalFileSystem(event);
            if (videoFileSize == 0)
                return;
        } finally {
            totalProcessingWatch.stopTiming();
        }
        long end = System.currentTimeMillis();
        if (enableStatistics) {
            PrintWriter statisticsWriter = null;
            try {
                statisticsWriter = new PrintWriter(new FileWriter(statisticsFile, true));
                long elapsedTime = (end - start) / 1000;
                double bytesPerSecond = videoFileSize / elapsedTime;
                // Video, Video file size, Time in s
                statisticsWriter.printf("%s,%d,%d,%.0f\n",
                        event.getMessageBody(), videoFileSize, elapsedTime, bytesPerSecond);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Can't write statistics", e);
            } finally {
                IOUtils.closeQuietly(statisticsWriter);
            }
        }
    }

    public long handleFromS3(SQSEvent event) {
        String objectKey = event.getMessageBody();
        File videoToConvert;
        try {
            videoToConvert = download(objectKey);
            if (videoToConvert == null)
                return 0;
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Can''t download video from event {0}", event);
            return -1;
        }
        List<File> videosConverted;
        try {
            videosConverted = convertVideos(videoToConvert, "flv");
        } catch (VideoConversionException e) {
            logger.log(Level.SEVERE, "Can''t convert video {0}", videoToConvert.getAbsolutePath());
            e.printStackTrace();
            return -1;
        } catch (TimeoutException e) {
            logger.log(Level.SEVERE, "Video conversion timed out!", e);
            return -1;
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Video conversion was interrupted!", e);
            return -1;
        }
        // upload the converted videos to s3
        try {
            upload(videosConverted);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Can''t upload video", e);
        }
        return videoToConvert.lastModified();
    }

    public long handleFromLocalFileSystem(SQSEvent event) {
        File videoToConvert = null;
        try {
            videoToConvert = new File(new URL(event.getMessageBody()).toURI());
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Can''t get URL for video from event {0}", event);
            return -1;
        }
        List<File> videosConverted;
        try {
            videosConverted = convertVideos(videoToConvert, "flv");
        } catch (VideoConversionException e) {
            logger.log(Level.SEVERE, "Can''t convert video {0}", videoToConvert.getAbsolutePath());
            e.printStackTrace();
            return -1;
        } catch (TimeoutException e) {
            logger.log(Level.SEVERE, "Video conversion timed out!", e);
            return -1;
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Video conversion was interrupted!", e);
            return -1;
        }
        return videoToConvert.length();
    }

    private File download(String key) throws IOException, S3ServiceException {
        logger.log(Level.FINE, "Downloading from bucket ''{0}'' file ''{1}''...", new Object[] { srcBucketName, key });
        downloadWatch.startTiming();
        S3Bucket bucket = s3.createBucket(new S3Bucket(srcBucketName));
        S3Object object;
        FileOutputStream fileStream = null;
        InputStream s3Stream = null;
        try {
            object = s3.getObject(bucket, key);
            File tmpFile = new File(temporaryStorageLocation, key);
            long start = System.currentTimeMillis();
            s3Stream = object.getDataInputStream();
            fileStream = new FileOutputStream(tmpFile);
            IOUtils.copyLarge(s3Stream, fileStream);
            long end = System.currentTimeMillis();
            logger.log(Level.INFO, "Downloaded from bucket ''{0}'' file ''{1}'' in {2}s...",
                    new Object[]{srcBucketName, key, ((end - start) / 1000)});
            return tmpFile;
        } catch (S3ServiceException e) {
            if (!"NoSuchKey".equals(e.getS3ErrorCode())) {
                throw e;
            } else {
                logger.log(Level.WARNING, "No such video: {0}. Skipping it.", e.getS3ErrorCode());
                return null;
            }
        } finally {
            IOUtils.closeQuietly(s3Stream);
            IOUtils.closeQuietly(fileStream);
            downloadWatch.stopTiming();
        }
    }

    private void upload(List<File> files) throws IOException, S3ServiceException {
        upload(files.toArray(new File[files.size()]));
    }

    private void upload(File... files) throws IOException, S3ServiceException {
        // create the destination bucket if needed
        S3Bucket bucket = s3.createBucket(new S3Bucket(destBucketName));
        uploadWatch.startTiming();
        // prepare s3 objects
        S3Object[] objects = new S3Object[files.length];
        long bytes = 0;
        for (int i = 0; i < files.length; i++) {
            String key = files[i].getName().replace(".mp4", ".flv");
            logger.log(Level.FINE, "Uploading video ''{0}'' ({1} bytes) into bucket ''{2}'' key ''{3}''...",
                    new Object[] { files[i].getAbsolutePath(), files[i].length(), destBucketName, key });
            objects[i] = new S3Object(bucket, key);
            objects[i].setDataInputFile(files[i]);
            objects[i].setContentType("video/x-flv");
            objects[i].setContentLength(files[i].length());
            objects[i].setLastModifiedDate(new Date(files[i].lastModified()));
            bytes += files[i].length();
        }

        // upload the videos
        try {
            long start = System.currentTimeMillis();
            multi.putObjects(bucket, objects);
            long end = System.currentTimeMillis();
            logger.log(Level.INFO,
                "Uploaded {0,choice,1#1 video|1<{0} videos} ({1} bytes) into bucket ''{2}'' in {3}s",
                new Object[] { files.length, bytes, destBucketName, ((end - start) / 1000) });
        } catch (S3ServiceException e) {
            throw new RemoteException("Unexpected error when uploading videos to the remote repository", e);
        }

        uploadWatch.stopTiming();
    }

    /**
     * Produces 4 variants from the original.
     * @param original the original video for which the variants should be produced
     * @throws VideoConversionException if the video can't be converted properly
     * @throws TimeoutException
     * @throws InterruptedException
     */
    public void convert(final File original, final String format)
            throws VideoConversionException, TimeoutException, InterruptedException {
        convertVideos(original, format);        
    }

    List<File> convertVideos(final File original, final String format)
            throws VideoConversionException, TimeoutException, InterruptedException {
        conversionWatch.startTiming();
        long start = System.currentTimeMillis();
        final String orig = original.getName();
        // first one: 82x61-96k-64k-15fps - 0 to 6 sec.
        Future<File> video1 = pool.submit(new Callable<File>() {
            public File call() throws VideoConversionException, InterruptedException {
                File dest = new File(original.getParent(), orig.substring(0, orig.lastIndexOf('.')) + "-96k." + format);
                return encoder.convertVideo(original, dest.getName(), format, 82, 61, 0, 6, 96, 64, 15);
            }
        });
        // second one: 260x195-350k-64k-25fps - 0 to 10 sec.
        Future<File> video2 = pool.submit(new Callable<File>() {
            public File call() throws VideoConversionException, InterruptedException {
                File dest = new File(original.getParent(), orig.substring(0, orig.lastIndexOf('.')) + "-350k." + format);
                return encoder.convertVideo(original, dest.getName(), format, 260, 195, 0, 10, 350, 64, 25);
            }
        });
        // third one: 320x240-450k-64k-25fps
        Future<File> video3 = pool.submit(new Callable<File>() {
            public File call() throws VideoConversionException, InterruptedException {
                File dest = new File(original.getParent(), orig.substring(0, orig.lastIndexOf('.')) + "-450k." + format);
                return encoder.convertVideo(original, dest.getName(), format, 320, 240, null, null, 450, 64, 25);
            }
        });
        // fourth one: 640x480-1200k-64k-25fps
        Future<File> video4 = pool.submit(new Callable<File>() {
            public File call() throws VideoConversionException, InterruptedException {
                File dest = new File(original.getParent(), orig.substring(0, orig.lastIndexOf('.')) + "-1200k." + format);
                return encoder.convertVideo(original, dest.getName(), format, 640, 480, null, null, 1200, 64, 25);
            }
        });
        try {
            List<File> videosConverted = new ArrayList<File>(3);
            videosConverted.add(getEncodedVideoForFuture(video1));
            videosConverted.add(getEncodedVideoForFuture(video2));
            videosConverted.add(getEncodedVideoForFuture(video3));
            videosConverted.add(getEncodedVideoForFuture(video4));
            long end = System.currentTimeMillis();
            conversionWatch.stopTiming();
            logger.log(Level.INFO,
                    "Converted {0} to {1,choice,1#1 video|1<{1} videos} as format {2} in {3}s...",
                    new Object[] { original, videosConverted.size(), format, ((end - start) / 1000)});
            return videosConverted;
        } catch (ExecutionException e) {
            throw new VideoConversionException("Can't execute video conversion", e);
        }
    }

    private File getEncodedVideoForFuture(Future<File> future)
            throws ExecutionException, InterruptedException, TimeoutException {
        try {
            return future.get(5 * 60, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            // try once more
            return future.get(5 * 60, TimeUnit.SECONDS);
        }
    }

}
