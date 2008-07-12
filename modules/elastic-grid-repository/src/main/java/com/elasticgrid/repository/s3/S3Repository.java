package com.elasticgrid.repository.s3;

import com.elasticgrid.model.Application;
import com.elasticgrid.model.Grid;
import com.elasticgrid.model.ec2.impl.EC2GridImpl;
import com.elasticgrid.model.internal.ApplicationImpl;
import com.elasticgrid.model.internal.RemoteStore;
import com.elasticgrid.repository.AbstractRepository;
import com.elasticgrid.repository.RemoteRepository;
import com.elasticgrid.repository.Repository;
import com.elasticgrid.utils.jibx.ObjectXmlMappingException;
import com.elasticgrid.utils.jibx.XmlUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.multithread.DownloadPackage;
import org.jets3t.service.multithread.S3ServiceSimpleMulti;
import org.jets3t.service.utils.ByteFormatter;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import static java.lang.String.format;
import java.rmi.RemoteException;

/**
 * Remote applications repository with storage on <a href="http://aws.amazon.com/s3">Amazon S3</a>.
 *
 * @author Jerome Bernard
 */
public class S3Repository extends AbstractRepository implements RemoteRepository, InitializingBean {
    private S3Service s3;
    private S3Bucket bucket;
    private String bucketName, bucketLocation;
    private S3ServiceSimpleMulti multi;
    private static final String REMOTE_REPOSITORY = "/repository";
    private static final String KEY_GLOBAL_CONFIGURATION = REMOTE_REPOSITORY + "/config/elastic-grid.xml";
    private static final String ENCODING = "UTF-8";

    public void bootstrap() throws RemoteException {
        logger.info("Initializing the Amazon S3 remote repository for usage with Elastic Grid...");
        // create the remote repository if needed
        try {
            logger.debug(format("Creating Amazon S3 bucket '%s'", bucket.getName()));
            s3.createBucket(bucket);
        } catch (S3ServiceException e) {
            throw new RemoteException("Unexpected error when initializing the remote repository", e);
        }
        // create an empty store
        store = new RemoteStore();
        // mark repository as initialized
        initialized = true;
    }

    public void load() throws RemoteException {
        if (!initialized) {
            logger.warn("Remote repository has not been bootstrapped. Bootstrapping it right now...");
            bootstrap();
        }
        // load the global configuration file
        InputStream stream = null;
        try {
            S3Object object = s3.getObject(bucket, KEY_GLOBAL_CONFIGURATION);
            stream = object.getDataInputStream();
            store = XmlUtils.convertXmlToObject("ElasticGrid", RemoteStore.class, stream, object.getContentEncoding());
        } catch (S3ServiceException e) {
            if ("NoSuchKey".equals(e.getS3ErrorCode())) {
                return;
            } else {
                throw new RemoteException("Can't download remote repository configuration", e);
            }
        } catch (ObjectXmlMappingException e) {
            throw new RemoteException("Can't parse remote repository configuration", e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
        // load the grids
        loadGrids();
        // load the applications
        loadApplications();
    }

    public void save() throws RemoteException {
        // save the global configuration file
        try {
            for (Grid grid : getGrids().values())
                store.grid(grid.getName());
            for (Application app : getApplications().values())
                store.application(app.getName());
            String data = XmlUtils.convertObjectToXml("ElasticGrid", store, ENCODING);
            logger.debug("Saving data into '{}':\n{}", KEY_GLOBAL_CONFIGURATION, data);
            S3Object object = new S3Object(bucket, KEY_GLOBAL_CONFIGURATION, data);
            s3.putObject(bucket, object);
        } catch (Exception e) {
            throw new RemoteException(format("Can't write repository configuration into '%s'",
                    KEY_GLOBAL_CONFIGURATION), e);
        }
        // save the grids
        saveGrids();
        // save the applications
        saveApplications();
    }

    private void loadGrids() throws RemoteException {
        for (String gridName : store.getGrids()) {
            // load each grid configuration
            InputStream stream = null;
            try {
                String gridKey = getGridKey(gridName);
                logger.info("Loading '%s' grid configuration from '%s'", gridName, gridKey);
                S3Object object = s3.getObject(bucket, gridKey);
                stream = object.getDataInputStream();
                Grid grid = XmlUtils.convertXmlToObject("ElasticGrid", EC2GridImpl.class, stream, object.getContentEncoding());
                grid(gridName, grid);
            } catch (S3ServiceException e) {
                throw new RemoteException(format("Can't download '%s' grid configuration", gridName), e);
            } catch (ObjectXmlMappingException e) {
                throw new RemoteException(format("Can't parse '%s' grid configuration", gridName), e);
            } finally {
                IOUtils.closeQuietly(stream);
            }
        }
    }

    private void saveGrids() throws RemoteException {
        for (Grid grid : getGrids().values()) {
            try {
                String gridKey = getGridKey(grid.getName());
                logger.info("Saving '%s' grid configuration into '%s'", grid.getName(), gridKey);
                String data = XmlUtils.convertObjectToXml("ElasticGrid", grid, ENCODING);
                S3Object object = new S3Object(bucket, gridKey, data);
                s3.putObject(bucket, object);
            } catch (ObjectXmlMappingException e) {
                throw new RemoteException(format("Can't generate '%s' grid configuration", grid.getName()), e);
            } catch (S3ServiceException e) {
                throw new RemoteException(format("Can't upload '%s' grid configuration", grid.getName()), e);
            } catch (UnsupportedEncodingException e) {
                throw new RemoteException(format("Unsupported string encoding '%s'", ENCODING), e);
            }
        }
    }

    private void loadApplications() throws RemoteException {
        for (String appName : store.getApplications()) {
            // load each application configuration
            InputStream stream = null;
            try {
                String appKey = getApplicationKey(appName);
                logger.info("Loading '%s' application configuration from '%s'", appName, appKey);
                S3Object object = s3.getObject(bucket, appKey);
                stream = object.getDataInputStream();
                Application application = XmlUtils.convertXmlToObject("ElasticGrid", ApplicationImpl.class, stream, object.getContentEncoding());
                application(application);
            } catch (S3ServiceException e) {
                throw new RemoteException(format("Can't download '%s' application configuration", appName), e);
            } catch (ObjectXmlMappingException e) {
                throw new RemoteException(format("Can't parse '%s' application configuration", appName), e);
            } finally {
                IOUtils.closeQuietly(stream);
            }
        }
    }

    private void saveApplications() throws RemoteException {
		for (Application app : getApplications().values()) {
            try {
                String appKey = getApplicationKey(app.getName());
                logger.info("Saving '%s' application configuration into '%s'", app.getName(), appKey);
                String data = XmlUtils.convertObjectToXml("ElasticGrid", app, ENCODING);
                S3Object object = new S3Object(bucket, appKey, data);
                s3.putObject(bucket, object);
            } catch (ObjectXmlMappingException e) {
                throw new RemoteException(format("Can't generate '%s' application configuration", app.getName()), e);
            } catch (S3ServiceException e) {
                throw new RemoteException(format("Can't upload '%s' application configuration", app.getName()), e);
            } catch (UnsupportedEncodingException e) {
                throw new RemoteException(format("Unsupported string encoding '%s'", ENCODING), e);
            }
        }
    }

    public void purge() throws RemoteException {
        logger.info(format("Purging the Amazon S3 remote repository from bucket '%s'", bucket.getName()));
        try {
            if (s3.isBucketAccessible(bucket.getName())) {
                S3Object[] objects = s3.listObjects(bucket);
                for (S3Object object : objects)
                    s3.deleteObject(bucket, object.getKey());
                s3.deleteBucket(bucket);
            } else {
                logger.warn(format("Amazon S3 bucket '%s' is not accessible. Skipping purge of the remote repository.", bucket.getName()));
            }
        } catch (S3ServiceException e) {
            throw new RemoteException("Can't purge the remote repository", e);
        }
    }

    public void restore() throws RemoteException {
        logger.info("Restoring the local repository from the Amazon S3 remote repository...");
        try {
            // ensure the bucket exists
            if (!s3.isBucketAccessible(bucket.getName())) {
                logger.warn(format("Skipping restoration because Amazon S3 bucket '%s' does not exist", bucket.getName()));
                return;
            }
            // download the files from the remote repository
            S3Object[] objects = s3.listObjects(bucket);
            DownloadPackage[] downloadPackages = new DownloadPackage[objects.length];
            for (int i = 0; i < objects.length; i++) {
                S3Object object = objects[i];
                File file = new File(getRoot(), object.getKey());
                logger.info(format("Downloading file %s [size is %s] as %s...", object.getKey(),
                        new ByteFormatter().formatByteSize(object.getContentLength()), file.getPath()));
                downloadPackages[i] = new DownloadPackage(object, file);
            }
            multi.downloadObjects(bucket, downloadPackages);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Unexpected error when downloading content from the remote repository", e);
        }
        // todo: convert the remote repository files to a local repository configuration
    }

    public String upload(File file) throws RemoteException, FileNotFoundException {
        String fileName = file.getAbsolutePath();
        String fileNameStripped = fileName.substring(
                fileName.indexOf(Repository.LOCAL_DIRECTORY) + Repository.LOCAL_DIRECTORY.length() + 1,
                fileName.length()).replace('\\', '/');
        String key = REMOTE_REPOSITORY + '/' + fileNameStripped;
        logger.info(format("Uploading %s into bucket '%s'", key, bucket.getName()));
        S3Object object = new S3Object(bucket, file);
        object.setKey(key);
        try {
            return s3.putObject(bucket, object).getKey();
        } catch (S3ServiceException e) {
            throw new RemoteException("Unexpected error when uploading content to the remote repository", e);
        }
    }

    private String getGridKey(String gridName) {
        return REMOTE_REPOSITORY + "/config/" + gridName + "/grid.xml";
    }

    private String getApplicationKey(String appName) {
        return REMOTE_REPOSITORY + "/applications" + appName + "/application.xml";
    }

    public void setS3(S3Service s3) {
        this.s3 = s3;
        this.multi = new S3ServiceSimpleMulti(s3);
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public void setBucketLocation(String bucketLocation) {
        this.bucketLocation = bucketLocation;
    }

    protected Logger getLogger() {
        return logger;
    }

    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isEmpty(bucketLocation))
            this.bucket = s3.createBucket(new S3Bucket(bucketName));
        else
            this.bucket = s3.createBucket(new S3Bucket(bucketName, bucketLocation));
    }
}
