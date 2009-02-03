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

import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.DiscoveryManagement;
import net.jini.lookup.LookupCache;
import org.rioproject.associations.AssociationMgmt;
import org.rioproject.associations.AssociationType;
import org.rioproject.associations.AssociationDescriptor;
import org.rioproject.core.provision.ProvisionManager;
import org.rioproject.opstring.OpStringManagerProxy;
import org.rioproject.resources.client.DiscoveryManagementPool;
import org.rioproject.resources.client.LookupCachePool;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.FileFilter;
import java.rmi.RMISecurityManager;
import java.security.Permission;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jerome Bernard
 */
public class Converter implements Runnable {
    /*
    @Option(name = "-a", usage = "AWS Access ID", required = false)
    private String accessId;

    @Option(name = "-s", usage = "AWS Secret Key", required = false)
    private String secretKey;

    @Option(name = "-b", usage = "S3 source bucket", required = true)
    private String bucket;

    @Option(name = "-q", usage = "SQS queue used for video conversion requests", required = true)
    private String queueName;
    */

    @Option(name = "-f", usage = "Target format for video conversion", required = false)
    private String format = "flv";

    @Argument(usage = "Directory where the videos to encode are located", required = true, multiValued = false)
    private String directory;

    private CmdLineParser parser;
    static VideoConverter converter;

    private static final Logger logger = Logger.getLogger(Converter.class.getName());

    public Converter() throws InterruptedException {
        /*
        Properties awsProperties = new Properties();
        File awsPropertiesFile = new File(System.getProperty("user.home") + File.separatorChar + ".eg", "aws.properties");
        InputStream stream = null;
        try {
            stream = new FileInputStream(awsPropertiesFile);
            awsProperties.load(stream);
        } catch (Exception e) {
            // do nothing -- this is expected behaviour
        } finally {
            IOUtils.closeQuietly(stream);
        }
        accessId = (String) awsProperties.get("aws.accessId");
        secretKey = (String) awsProperties.get("aws.secretKey");
        */

        // define associations
        AssociationDescriptor videoConverterAssociation = new AssociationDescriptor(
                AssociationType.USES, "Video Converter"
        );
        videoConverterAssociation.setMatchOnName(true);
        videoConverterAssociation.setInterfaceNames(VideoConverter.class.getName());
        videoConverterAssociation.setGroups("rio");

        // define associations management
        AssociationMgmt associationMgmt = new AssociationMgmt();
        associationMgmt.register(new VideoConverterAssociationListener());
        associationMgmt.addAssociationDescriptors(videoConverterAssociation);

        while (converter == null) {
            System.out.printf("Waiting for video converter...\n");
            Thread.sleep(1000);
        }
    }

    public void bootstrap() throws Exception {
        ServiceTemplate template = new ServiceTemplate(null, new Class[] {ProvisionManager.class}, null);
        DiscoveryManagement discoMgr = DiscoveryManagementPool.getInstance().getDiscoveryManager(
                null,
                new String[]{"rio", "javaone"},
                null
        );
        LookupCache lookupCache = LookupCachePool.getInstance().getLookupCache(discoMgr, template);
        while (lookupCache.lookup(null) == null) {
            System.out.printf("Waiting to discover Provision Manager...\n");
            Thread.sleep(1000);
        }
        OpStringManagerProxy.setDiscoveryManagement(discoMgr);
    }

    public static void main(String[] args) throws InterruptedException {
        System.setSecurityManager(new RMISecurityManager() {
            public void checkPermission(Permission perm) {
                // do nothing -- hence approve everything
            }
        });
        Converter converter = new Converter();
        converter.parser = new CmdLineParser(converter);
        converter.parse(args);
    }

    public void parse(String... args) {
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            usage(e.getMessage());
        }
//        converter = new S3VideoConverterProxy(new VideoConverterJSB(), UuidFactory.generate(),
//                queueName, bucket, accessId, secretKey);
        run();
    }

    public void run() {
        try {
            File videosDirectory = new File(directory);
            logger.log(Level.INFO, "Converting videos from directory {0}", directory);
            File[] videos = videosDirectory.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    return !file.isDirectory() && file.getName().endsWith(".mp4");
                }
            });
            long start = System.currentTimeMillis();
            for (File video : videos)
                converter.convert(video, format);
            long end = System.currentTimeMillis();
            logger.log(Level.INFO,
                    "{0,choice,0#No files|1#One file|1<{0} files} successfully submitted in {1}s.",
                    new Object[] { videos.length, ((end - start) / 1000) });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void usage(String message) {
        System.err.printf("%s\nconverter [options...] VAL\n", message);
        parser.printUsage(System.err);
        System.exit(-1);
    }

}
