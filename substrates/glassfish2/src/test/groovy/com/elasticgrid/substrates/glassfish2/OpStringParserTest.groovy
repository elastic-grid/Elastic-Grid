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
package com.elasticgrid.substrates.glassfish2

import org.rioproject.core.provision.StagedSoftware
import org.rioproject.opstring.GroovyDSLOpStringParser
import org.rioproject.opstring.OpString
import org.rioproject.opstring.OpStringParser
import com.elasticgrid.opstring.ElasticGridDSLOpStringParser

class OpStringParserTest extends GroovyTestCase {
    def OpStringParser rioParser = new GroovyDSLOpStringParser()
    def OpStringParser egParser = new ElasticGridDSLOpStringParser()

    void testXmlParserOnGlassFishDeployment() {
//        testXmlParserOnGlassFishDeploymentFromFile rioParser, new File("src/test/resources/glassfishWithRio.groovy")
        testXmlParserOnGlassFishDeploymentFromFile egParser, new File("src/test/resources/glassfishWithElasticGrid.groovy")
    }
    void testXmlParserOnGlassFishDeploymentFromFile(parser, file) {
        def opstrings = parser.parse(file, null, false, null, null, null, false, null)
        assertEquals "There should be one and only one opstring", 1, opstrings.size()
        OpString opstring = opstrings[0]
        assertEquals "The OpString name is not valid", 'My Sample Webapp', opstring.name
        assertEquals "The number of services does not match", 1, opstring.services.size()
        def service = opstring.services[0]
        assertEquals "The name of the service is invalid", 'GlassFish', service.name

        def systemComponents = service.serviceLevelAgreements.systemRequirements.systemComponents
        assertEquals 1, systemComponents.size()
        assertEquals 'GlassFish', systemComponents[0].attributes['Name']
        assertEquals 'v2ur2-b04', systemComponents[0].attributes['Version']
        def softwareLoads = systemComponents[0].stagedSoftware

        assertEquals 'Expected 1 software load', 1, softwareLoads.length
        def StagedSoftware glassfish = softwareLoads[0]
        assertTrue "Data should be removed on service destroy", glassfish.removeOnDestroy()
        assertEquals 'https://elastic-grid.s3.amazonaws.com/glassfish/glassfish-v2ur2-b04.zip', glassfish.location.toString()
        assertEquals '${RIO_HOME}/system/external/glassfish', glassfish.installRoot
        assertTrue glassfish.unarchive()

        System.setProperty("RIO_HOME", '.')
        assertTrue service.stagedData[0].removeOnDestroy()
        assertEquals 'https://javaone-demo.s3.amazonaws.com/video-conversion-oar/video-conversion.war', service.stagedData[0].location.toString()
        assertEquals '${RIO_HOME}/system/external/glassfish/glassfish-v2ur2-b04/webapps', service.stagedData[0].installRoot
        def postInstall = glassfish.postInstallAttributes
        assertFalse postInstall.execDescriptor.useNoHup()
        assertEquals '/bin/chmod', postInstall.execDescriptor.commandLine
        assertEquals '+x ${RIO_HOME}/system/external/glassfish/glassfish-v2ur2-b04/bin/*.sh', postInstall.execDescriptor.inputArgs

        assertEquals 'bin', service.execDescriptor.workingDirectory
        assertEquals 'catalina.sh', service.execDescriptor.commandLine
        assertEquals 'run', service.execDescriptor.inputArgs
        
        assertEquals 1, service.planned
    }

}