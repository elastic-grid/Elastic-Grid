/**
 * Elastic Grid
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
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

deployment(name: 'JetBrains TeamCity') {
  groups('rio')
//    codebase('https://elastic-cluster-examples.s3.amazonaws.com/')

  // TeamCity Server
  tomcat(version: '6.0.18', removeOnDestroy: false) {
    webapp source: 'file://${EG_HOME}/deploy/TeamCity-4.0.war'
  }

  // TeamCity Agents
  serviceExec(name: 'TeamCity Agent') {
    software(name: 'TeamCity Agent', removeOnDestroy: false) {
      install source: 'file://${EG_HOME}/deploy/teamcity-build-agent-4.0.zip', target: 'teamcity', unarchive: true
      /*
      postInstall {
        execute command: "/bin/chmod +x \${EG_HOME}/system/external/teamcity/apache-tomcat-$version/bin/*.sh"
      }
      */
    }
    execute command: 'bin/agent.sh run'
    maintain 1
  }

}
