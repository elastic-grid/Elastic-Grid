import java.util.logging.Level

deployment(name:'Video Conversion Example') {
    groups('rio')

    logging {
        logger('com.elasticgrid.examples.video', Level.FINE) {
            handler 'java.util.logging.ConsoleHandler', Level.FINE
        }
        logger('com.elasticgrid.amazon.sqs', Level.FINE) {
            handler 'java.util.logging.ConsoleHandler', Level.FINE
        }
        logger('com.elasticgrid.amazon.ec2', Level.FINE) {
            handler 'java.util.logging.ConsoleHandler', Level.FINE
        }
    }

    service(name: 'Video Converter') {
        interfaces {
            classes 'com.elasticgrid.examples.video.VideoConverter'
            resources 'video-conversion-oar/lib-dl/video-conversion-converter-1.0-SNAPSHOT-dl.jar',
                      'elastic-grid/framework/amazon-sqs-1.0-SNAPSHOT.jar',
                      'elastic-grid/kernel/typica-1.2.jar',
                      'elastic-grid/kernel/jets3t-0.6.0.jar'

        }
        implementation(class: 'com.elasticgrid.examples.video.VideoConverterJSB') {
            resources 'video-conversion-oar/lib/video-conversion-converter-1.0-SNAPSHOT-impl.jar',
                      'video-conversion-oar/lib/video-conversion-converter-1.0-SNAPSHOT.jar'
        }
        configuration '''
            import java.io.File;
            com.elasticgrid.examples.video.VideoConverter {
                encoder = new com.elasticgrid.examples.video.MencoderEncoder(false);
                threadPoolSize = Integer.valueOf(4);
                sourceVideosBucket = "viv-src";
                encodedVideosBucket = "viv-dest";
                enableS3 = Boolean.TRUE;
                statistics = new File("video-conversion.csv");
            }
            com.elasticgrid.amazon.sqs.SQS {
                queueName = "VideoConversion";
                visibilityTimeout = new Integer(600);   // visibility timeout of 10 minutes
                watchPeriod = new Long(60000L);          // every minute - 60 seconds
            }
            com.elasticgrid.amazon.ec2 {
                amazonImageID = "ami-3d7a9f54";
                keyName = "eg-gsg-keypair";
                groups = java.util.Arrays.asList(new String[] {"elastic-grid", "default"});
                secured = Boolean.TRUE;
                publicAddress = Boolean.TRUE;
            }
        '''
        serviceLevelAgreements {
            sla(id: 'Queue VideoConversion', low: 2, high: 5) {
                policy(type: 'scaling', max: 10, lowerDampener: 3600, upperDampener: 3600)
            }
        }
        maintain 1
        maxPerMachine 1
    }

    tomcat(version:'6.0.16', removeOnDestroy: true) {
        webapp source:'https://javaone-demo.s3.amazonaws.com/video-conversion-oar/video-conversion.war'
    }

}