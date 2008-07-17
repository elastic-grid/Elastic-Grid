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

package com.elasticgrid.tools.cli.old;

import com.elasticgrid.tools.cli.AbstractCommand;

import java.rmi.RemoteException;

/**
 * Synchronize local file-system with remote applications repository.
// * @see Repository#synchronize()
 * @author Jerome Bernard
 */
public class SynchronizeCommand extends AbstractCommand {
//    private RepositoryManager repositoryManager;

    public void execute(String... args) throws IllegalArgumentException, RemoteException {
        //repositoryManager.synchronize();`
        throw new UnsupportedOperationException();
    }

//    public void setRepositoryManager(RepositoryManager repositoryManager) {
//        this.repositoryManager = repositoryManager;
//    }
}
