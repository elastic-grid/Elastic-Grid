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

import com.elasticgrid.tools.cli.AbstractCommand;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Jerome Bernard
 */
public abstract class AbstractApplicationCommand extends AbstractCommand {
//    protected RepositoryManager repositoryManager;

    abstract void execute(String applicationName, List<String> args) throws RemoteException;

    public void execute(String... args) throws RemoteException {
        String applicationName = args[0];
        execute(applicationName, Arrays.asList(args).subList(1, args.length));
    }

//    public void setRepositoryManager(RepositoryManager repositoryManager) {
//        this.repositoryManager = repositoryManager;
//    }
}