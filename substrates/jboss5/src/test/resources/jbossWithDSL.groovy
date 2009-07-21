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
deployment(name: 'My Sample Webapp') {
  groups('elastic-grid')
  jboss(removeOnDestroy: true) {
    application source: 'https://javaone-demo.s3.amazonaws.com/video-conversion-oar/video-conversion.war'

    // monitor maximum time spent on a HTTP call
    sla(id: 'active-thread-count', high: 100) {
      policy type: 'notify'
      monitor name: 'Active Thread Count',
              objectName: 'jboss.system:type=ServerInfo',
<<<<<<< HEAD:substrates/jboss5/src/test/resources/jbossWithDSL.groovy
              attribute: 'ActiveThreadCount', period: 1000
=======
              attribute: 'activeThreadCount', period: 1000
>>>>>>> Refactored a bit the JBoss and Tomcat substrates so that SLA requirements are exhibited in the end-user OpString and not "forced" by the Substrate.:substrates/jboss5/src/test/resources/jbossWithDSL.groovy
    }

    maintain 1
    maxPerMachine 1
  }
}
