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

package com.elasticgrid.tools.cli.old.app;

import java.util.List;
import java.rmi.RemoteException;

/**
 * Destroy a grid application.
 * @author Jerome Bernard
 */
public class DestroyApplicationCommand extends AbstractApplicationCommand {
    void execute(String applicationName, List<String> args) throws RemoteException {
//        repositoryManager.destroyApplication(applicationName);
    }
}
