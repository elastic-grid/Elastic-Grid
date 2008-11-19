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

deployment(name:'REST Container') {
    groups 'rio'

    logging {
        logger('com.elasticgrid.rest.RestJSB', Level.FINE)
        logger('org.springframework', Level.FINE)
    }

    systemRequirements(id: 'Elastic Grid Platform') {
        software name: 'Elastic Grid Kernel', version: '1.0'
        software name: 'Elastic Grid Framework', version: '1.0'
    }

    spring(name: 'application', config: '/com/elasticgrid/rest/applicationContext.xml') {
//        interfaces {
//            classes 'org.rioproject.resources.servicecore.Service'
//            resources 'rio-dl.jar'
//        }
        implementation(class: 'com.elasticgrid.rest.RestApplication') {
            resources 'rest-api-oar/lib/rest-api-service-0.8.2-impl.jar',
                      'rest-api-oar/lib/org.restlet-1.1-SNAPSHOT.jar',
                      'rest-api-oar/lib/org.restlet.ext.spring-1.1-SNAPSHOT.jar',
                      'rest-api-oar/lib/org.restlet.ext.jibx-1.1-SNAPSHOT.jar',
                      'rest-api-oar/lib/org.restlet.ext.wadl-1.1-SNAPSHOT.jar',
                      'rest-api-oar/lib/com.noelios.restlet-1.1-SNAPSHOT.jar',
                      'rest-api-oar/lib/com.noelios.restlet-ext.jetty.1.1-SNAPSHOT.jar',
                      'rest-api-oar/lib/jetty-6.1.11.jar',
                      'rest-api-oar/lib/jetty-ajp-6.1.11.jar',
                      'rest-api-oar/lib/jetty-sslengine-6.1.11.jar',
                      'rest-api-oar/lib/jetty-util-6.1.11.jar'
        }
        serviceLevelAgreements {
            systemRequirements ref: 'Elastic Grid Platform'
//            sla(id: 'Queue VideoConversion', low: 2, high: 5) {
//                policy(type: 'scaling', max: 10, lowerDampener: 3600, upperDampener: 3600)
//            }
        }
        maintain 1
        maxPerMachine 1
    }

/*
    service(name: 'REST Service') {
//        interfaces {
//            classes 'org.rioproject.resources.servicecore.Service'
//            resources 'rio-dl.jar'
//        }
        implementation(class: 'com.elasticgrid.rest.RestJSB') {
            resources 'rest-api-oar/lib/rest-api-service-0.8.2-impl.jar',
                      'rest-api-oar/lib/org.restlet-1.1-SNAPSHOT.jar',
                      'rest-api-oar/lib/org.restlet.ext.spring-1.1-SNAPSHOT.jar',
                      'rest-api-oar/lib/org.restlet.ext.jibx-1.1-SNAPSHOT.jar',
                      'rest-api-oar/lib/org.restlet.ext.wadl-1.1-SNAPSHOT.jar',
                      'rest-api-oar/lib/com.noelios.restlet-1.1-SNAPSHOT.jar',
                      'rest-api-oar/lib/com.noelios.restlet-ext.jetty.1.1-SNAPSHOT.jar',
                      'rest-api-oar/lib/jetty-6.1.11.jar',
                      'rest-api-oar/lib/jetty-ajp-6.1.11.jar',
                      'rest-api-oar/lib/jetty-sslengine-6.1.11.jar',
                      'rest-api-oar/lib/jetty-util-6.1.11.jar'
        }
        serviceLevelAgreements {
            systemRequirements ref: 'Elastic Grid Platform'
//            sla(id: 'Queue VideoConversion', low: 2, high: 5) {
//                policy(type: 'scaling', max: 10, lowerDampener: 3600, upperDampener: 3600)
//            }
        }
        maintain 1
        maxPerMachine 1
    }
*/
}