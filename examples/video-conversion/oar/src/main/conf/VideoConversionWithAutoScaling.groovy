/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This file is part of Elastic Grid.
 * 
 * Elastic Grid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * 
 * Elastic Grid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Elastic Grid.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.logging.Level

deployment(name:'Video Conversion Example') {
    groups('rio')
    codebase('https://elastic-grid-examples.s3.amazonaws.com/')

    systemRequirements(id: 'Elastic Grid Platform') {
        software name: 'Elastic Grid Kernel', version: '1.0'
        software name: 'Elastic Grid Framework', version: '1.0'
    }

//    logging {
//        logger('com.elasticgrid.examples.video', Level.FINE)
//        logger('com.elasticgrid.amazon.sqs', Level.FINE)
//        logger('com.elasticgrid.amazon.ec2', Level.FINE)
//    }

    service(name: 'Video Converter') {
        interfaces {
            classes 'com.elasticgrid.examples.video.VideoConverter'
            resources 'video-conversion-oar/lib-dl/video-conversion-converter-0.8.2-dl.jar',
                      'elastic-grid/framework/amazon-sqs-0.8.2.jar',
                      'elastic-grid/kernel/typica-1.4.1.jar',
                      'elastic-grid/kernel/jets3t-0.6.1.jar'

        }
        implementation(class: 'com.elasticgrid.examples.video.VideoConverterJSB') {
            resources 'video-conversion-oar/lib/video-conversion-converter-0.8.2-impl.jar',
                      'video-conversion-oar/lib/video-conversion-converter-0.8.2.jar'
        }

        configuration(
            'com.elasticgrid.examples.video.VideoConverter': [
                encoder: 'new com.elasticgrid.examples.video.MencoderEncoder(true)',
                threadPoolSize: 'Integer.valueOf(4)',
                sourceVideosBucket: '"elastic-grid-examples-video-src"',
                encodedVideosBucket: '"elastic-grid-examples-video-dest"',
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
            systemRequirements ref: 'Elastic Grid Platform'
            sla(id: 'Queue VideoConversion', low: 2, high: 5) {
                policy type: 'scaling', class: 'com.elasticgrid.amazon.ec2.sla.EC2ScalingPolicyHandler',
                       max: 3, lowerDampener: 3600, upperDampener: 3600
            }
        }
        maintain 1
        maxPerMachine 1
    }

    tomcat(version:'6.0.16', removeOnDestroy: true) {
        webapp source:'https://elastic-grid-examples.s3.amazonaws.com/video-conversion-oar/video-conversion-web-0.8.2.war'
    }

}