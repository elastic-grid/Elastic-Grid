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
package com.elasticgrid.substrates.tomcat6

import org.rioproject.core.provision.StagedSoftware
import org.rioproject.opstring.GroovyDSLOpStringParser
import org.rioproject.opstring.OpString
import org.rioproject.opstring.OpStringParser
import com.elasticgrid.opstring.ElasticGridDSLOpStringParser

class OpStringParserTest extends GroovyTestCase {
  def OpStringParser rioParser = new GroovyDSLOpStringParser()
  def OpStringParser egParser = new ElasticGridDSLOpStringParser()
  def version = '6.0.20'

  void testSubstrateAvailability() {
    URL url = "https://elastic-grid-substrates.s3.amazonaws.com/tomcat/apache-tomcat-${version}.zip".toURL()
    println "Testing Substrate availability on $url"
    try {
      url.newReader().read()
    } catch (IOException) {
      fail "Could not download Substrate from $url"
    }
  }

  void testTomcatDSL() {
    testXmlParserOnTomcatDeploymentFromFile rioParser, new File("src/test/resources/tomcatWithoutDSL.groovy")
    testXmlParserOnTomcatDeploymentFromFile egParser, new File("src/test/resources/tomcatWithDSL.groovy")
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
    assertEquals version, systemComponents[0].attributes['Version']
    def softwareLoads = systemComponents[0].stagedSoftware

    assertEquals 'Expected 1 software load', 1, softwareLoads.length
    def StagedSoftware tomcat = softwareLoads[0]
    assertTrue "Data should be removed on service destroy", tomcat.removeOnDestroy()
    assertEquals "https://elastic-grid-substrates.s3.amazonaws.com/tomcat/apache-tomcat-${version}.zip", tomcat.location.toString()
    assertEquals 'tomcat', tomcat.installRoot
    assertTrue tomcat.unarchive()

    System.setProperty("EG_HOME", '.')
    assertTrue service.stagedData[0].removeOnDestroy()
    assertEquals 'https://javaone-demo.s3.amazonaws.com/video-conversion-oar/video-conversion.war', service.stagedData[0].location.toString()
    assertEquals "tomcat/apache-tomcat-${version}/webapps", service.stagedData[0].installRoot
    def postInstall = tomcat.postInstallAttributes
//    assertFalse postInstall.execDescriptor.useNoHup()
    assertEquals '/bin/chmod', postInstall.execDescriptor.commandLine
    assertEquals "+x bin/*.sh", postInstall.execDescriptor.inputArgs

    assertEquals 'bin', service.execDescriptor.workingDirectory
    assertEquals 'catalina.sh', service.execDescriptor.commandLine
    assertEquals 'run', service.execDescriptor.inputArgs

    assertEquals 1, service.planned
  }

}