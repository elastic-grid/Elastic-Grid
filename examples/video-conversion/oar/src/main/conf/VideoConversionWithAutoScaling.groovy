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

import java.util.logging.Level

deployment(name:'Video Conversion Example') {
    groups('rio')
    codebase('https://elastic-cluster-examples.s3.amazonaws.com/')

    systemRequirements(id: 'Elastic Cluster Platform') {
        software name: 'Elastic Cluster Kernel', version: '1.0'
        software name: 'Elastic Cluster Framework', version: '1.0'
    }

//    logging {
//        logger('com.elasticgrid.examples.video', Level.FINE)
//        logger('com.elasticgrid.amazon.sqs', Level.FINE)
//        logger('com.elasticgrid.amazon.ec2', Level.FINE)
//    }

    service(name: 'Video Converter') {
        interfaces {
            classes 'com.elasticgrid.examples.video.VideoConverter'
            resources 'video-conversion-oar/lib-dl/video-conversion-converter-0.8.3-dl.jar',
                      'elastic-cluster/framework/amazon-sqs-0.8.3.jar',
                      'elastic-cluster/kernel/typica-1.4.1.jar',
                      'elastic-cluster/kernel/jets3t-0.7.0.jar'

        }
        implementation(class: 'com.elasticgrid.examples.video.VideoConverterJSB') {
            resources 'video-conversion-oar/lib/video-conversion-converter-0.8.3-impl.jar',
                      'video-conversion-oar/lib/video-conversion-converter-0.8.3.jar'
        }

        configuration(
            'com.elasticgrid.examples.video.VideoConverter': [
                encoder: 'new com.elasticgrid.examples.video.MencoderEncoder(true)',
                threadPoolSize: 'Integer.valueOf(4)',
                sourceVideosBucket: '"elastic-cluster-examples-video-src"',
                encodedVideosBucket: '"elastic-cluster-examples-video-dest"',
                enableS3: 'Boolean.TRUE',
                statistics: 'new java.io.File("video-conversion.csv")'
            ],
            'com.elasticgrid.amazon.sqs.SQS': [
                queueName: '"VideoConversion"',
                visibilityTimeout: 'new Integer(600)',
                watchPeriod: 'new Long(60000L)'
            ]
        )

        serviceLevelAgreements {
            systemRequirements ref: 'Elastic Cluster Platform'
            sla(id: 'Queue VideoConversion', low: 2, high: 5) {
                policy type: 'scaling', class: 'com.elasticgrid.amazon.ec2.sla.EC2ScalingPolicyHandler',
                       max: 3, lowerDampener: 3600, upperDampener: 3600
            }
        }
        maintain 1
        maxPerMachine 1
    }

    tomcat(version:'6.0.16', removeOnDestroy: true) {
        webapp source:'https://elastic-cluster-examples.s3.amazonaws.com/video-conversion-oar/video-conversion-web-0.8.3.war'
    }

}