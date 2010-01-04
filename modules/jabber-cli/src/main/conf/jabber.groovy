/**
 * Elastic Grid
 * Copyright (C) 2008-2010 Elastic Grid, LLC.
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
deployment(name: 'CLI Jabber Gateway') {
  groups('rio')

  service(name: 'CLI Jabber Gateway') {
    interfaces {
      classes 'com.elasticgrid.tools.cli.jabber.JabberCLI'
      resources 'jabber/jabber-cli-0.9.3-dl.jar'

    }
    implementation(class: 'com.elasticgrid.tools.cli.jabber.JabberCLIJSB') {
      resources 'jabber/jabber-cli-0.9.3.jar',
                'jabber/smack-3.0.4.jar',
                'jabber/smackx-3.0.4.jar',
                'elastic-grid/kernel/elastic-grid-cli-0.9.3.jar'
    }

    configuration '''
        com.elasticgrid.tools.cli.jabber {
            jabberUser = "ec2";
            jabberDomain = "elastic-grid.com";
            jabberPassword = "elasticgrid";
            egAdministrator = "jerome.bernard@gmail.com";
            jabberServer = "talk.google.com";
            jabberPort = "5222";
        }
    '''

    maintain 1
    maxPerMachine 1
  }
}