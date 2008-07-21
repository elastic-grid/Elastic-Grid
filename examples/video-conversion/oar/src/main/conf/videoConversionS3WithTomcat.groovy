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

import java.util.logging.Level

deployment(name:'Video Conversion Example') {
    groups('rio')

    systemRequirements(id: 'Elastic Grid Platform') {
        software name: 'Elastic Grid Kernel', version: '1.0'
        software name: 'Elastic Grid Framework', version: '1.0'
    }

    logging {
        logger('com.elasticgrid.examples.video', Level.FINE)
        logger('com.elasticgrid.amazon.sqs', Level.FINE)
        logger('com.elasticgrid.amazon.ec2', Level.FINE)
    }

    service(name: 'Video Converter') {
        interfaces {
            classes 'com.elasticgrid.examples.video.VideoConverter'
            resources 'video-conversion-oar/lib-dl/video-conversion-converter-1.0-SNAPSHOT-dl.jar',
                      'elastic-grid/framework/amazon-sqs-1.0-SNAPSHOT.jar',
                      'elastic-grid/kernel/typica-1.3.jar',
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
            systemRequirements ref: 'Elastic Grid Platform'
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