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

package com.elasticgrid.opstring

import org.rioproject.core.provision.StagedSoftware
import org.rioproject.opstring.GroovyDSLOpStringParser
import org.rioproject.opstring.OpString
import org.rioproject.opstring.OpStringParser

class OpStringParserTest extends GroovyTestCase {
    def OpStringParser rioParser = new GroovyDSLOpStringParser()
    def OpStringParser egParser = new ElasticGridDSLOpStringParser()

    void testXmlParserOnTomcatDeployment() {
        testXmlParserOnTomcatDeploymentFromFile rioParser, new File("src/test/resources/tomcatWithRio.groovy")
        testXmlParserOnTomcatDeploymentFromFile egParser, new File("src/test/resources/tomcatWithElasticGrid.groovy")
    }
    void testXmlParserOnTomcatDeploymentFromFile(parser, file) {
        def opstrings = parser.parse(file, null, false, null, null, null, false, null)
        assertEquals "There should be one and only one opstring", 1, opstrings.size()
        OpString opstring = opstrings[0]
        assertEquals "The OpString name is not valid", 'My Sample Webapp', opstring.name
        assertEquals "The number of services does not match", 1, opstring.services.size()
        def service = opstring.services[0]
        assertEquals "The name of the service is invalid", "Tomcat", service.name

        def systemComponents = service.serviceLevelAgreements.systemRequirements.systemComponents
        assertEquals 1, systemComponents.size()
        assertEquals 'Tomcat', systemComponents[0].attributes['Name']
        assertEquals '6.0.16', systemComponents[0].attributes['Version']
        def softwareLoads = systemComponents[0].stagedSoftware

        assertEquals 'Expected 1 software load', 1, softwareLoads.length
        def StagedSoftware tomcat = softwareLoads[0]
        assertTrue "Data should be removed on service destroy", tomcat.removeOnDestroy()
        assertEquals 'https://elastic-grid.s3.amazonaws.com/tomcat/apache-tomcat-6.0.16.zip', tomcat.location.toString()
        assertEquals '${RIO_HOME}/system/external/tomcat', tomcat.installRoot
        assertTrue tomcat.unarchive()

        System.setProperty("RIO_HOME", '.')
        def postInstall = tomcat.postInstallAttributes
        assertTrue postInstall.stagedData.removeOnDestroy()
        assertEquals 'https://javaone-demo.s3.amazonaws.com/video-conversion-oar/video-conversion.war', postInstall.stagedData.location.toString()
        assertEquals '\${RIO_HOME}/system/external/tomcat/apache-tomcat-6.0.16/webapps', postInstall.stagedData.installRoot
        assertFalse postInstall.execDescriptor.useNoHup()
        assertEquals '/bin/chmod', postInstall.execDescriptor.commandLine
        assertEquals '+x ./system/external/tomcat/apache-tomcat-6.0.16/bin/*.sh', postInstall.execDescriptor.inputArgs

        assertEquals 'bin', service.execDescriptor.workingDirectory
        assertEquals 'catalina.sh', service.execDescriptor.commandLine
        assertEquals 'run', service.execDescriptor.inputArgs
        
        assertEquals 1, service.planned
    }

}