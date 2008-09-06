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

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.rmi.RMISecurityManager;
import java.security.Permission;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;

import com.xerox.amazonws.sqs2.MessageQueue;
import com.xerox.amazonws.sqs2.SQSUtils;
import com.xerox.amazonws.sqs2.SQSException;

/**
 * @author Jerome Bernard
 */
public class RemoteConverter implements Runnable {
    @Option(name = "-a", usage = "AWS Access ID", required = true)
    private String accessId;

    @Option(name = "-s", usage = "AWS Secret Key", required = true)
    private String secretKey;

    @Option(name = "-q", usage = "SQS queue used for video conversion requests", required = true)
    private String queueName;

    @Option(name = "-f", usage = "Target format for video conversion", required = false)
    private String format = "flv";

    @Argument(usage = "Video filename", required = true, multiValued = true)
    private List<String> videos;

    private CmdLineParser parser;
    static VideoConverter converter;

    private static final Logger logger = Logger.getLogger(Converter.class.getName());


    public static void main(String[] args) throws InterruptedException {
        System.setSecurityManager(new RMISecurityManager() {
            public void checkPermission(Permission perm) {
                // do nothing -- hence approve everything
            }
        });
        RemoteConverter converter = new RemoteConverter();
        converter.parser = new CmdLineParser(converter);
        converter.parse(args);
    }

    public void parse(String... args) {
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            usage(e.getMessage());
        }
        run();
    }

    public void run() {
        try {
            try {
                for (String video : videos) {
                    logger.log(Level.FINE, "Sending video conversion request for ''{0}''...", video);
                    MessageQueue queue = SQSUtils.connectToQueue(queueName, accessId, secretKey);
                    queue.sendMessage(video);
                }
            } catch (SQSException e) {
                throw new VideoConversionException("Can't convert video", e);
            }
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