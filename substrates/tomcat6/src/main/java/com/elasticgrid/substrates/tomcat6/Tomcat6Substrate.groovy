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

import com.elasticgrid.substrates.AbstractSubstrate
import groovy.xml.MarkupBuilder
import com.elasticgrid.substrates.FirewallRule
import com.elasticgrid.substrates.FirewallRule.IpProtocol

class Tomcat6Substrate extends AbstractSubstrate {

  def defaultVersion = '6.0.20'

  public String getName() {
    return "Tomcat 6"
  }

  public void addDomainSpecificLanguageFeatures(MarkupBuilder builder, ExpandoMetaClass emc) {
    emc.tomcat = {Map attributes, Closure cl ->
      version = attributes.version ?: defaultVersion
      def removeOnDestroy = attributes.removeOnDestroy ?: true
      serviceExec(name: 'Tomcat') {
        software(name: 'Tomcat', version: version, removeOnDestroy: removeOnDestroy) {
          install source: "https://elastic-grid-substrates.s3.amazonaws.com/tomcat/apache-tomcat-${version}.zip",
                  target: 'tomcat', unarchive: true
          postInstall(removeOnCompletion: removeOnDestroy) {
            execute command: "/bin/chmod +x tomcat/apache-tomcat-$version/bin/*.sh"
          }
        }
        execute inDirectory: 'bin', command: 'catalina.sh run'
        cl()
        maintain 1
        maxPerMachine 1
      }
    }
    emc.webapp = {Map attributes ->
      data source: attributes.source,
              target: "tomcat/apache-tomcat-$version/webapps"
    }
  }

  /**
   * Open port 8080 for TCP from anywhere.
   * @todo temporary situation until we have the Apache frontend working fine.
   */
  @Override
  public List<FirewallRule> getFirewallRules() {
    [new FirewallRule(getName(), IpProtocol.TCP, 8080, '??')]
  }

}
