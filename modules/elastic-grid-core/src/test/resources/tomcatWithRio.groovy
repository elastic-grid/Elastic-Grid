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

opstring(name:'My Sample Webapp') {
    groups('rio')
    serviceExec(name:'Tomcat') {
        software(name:'Tomcat', version:'6.0.16', removeOnDestroy: true) {
            download source:'https://elastic-grid.s3.amazonaws.com/tomcat/apache-tomcat-6.0.16.zip',
                     installRoot:'${RIO_HOME}/system/external/tomcat', unarchive: true
            postInstall(removeOnCompletion: true) {
                download source:'https://elastic-grid.s3.amazonaws.com/tomcat/sample.war',
                         installRoot:'${RIO_HOME}/system/external/tomcat/apache-tomcat-6.0.16/webapps'
                execute command:'/bin/chmod +x ${RIO_HOME}/system/external/tomcat/apache-tomcat-6.0.16/bin/*sh',
                        nohup: false
            }
        }
        execute inDirectory:'bin', command: 'catalina.sh run'
        maintain 1
    }
}
