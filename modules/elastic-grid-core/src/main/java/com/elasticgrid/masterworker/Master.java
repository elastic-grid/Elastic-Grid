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

package com.elasticgrid.masterworker;

import com.elasticgrid.Task;
import com.elasticgrid.Job;
import com.elasticgrid.space.TaskResult;

import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Master of the Master Worker pattern.
 * @author Jerome Bernard
 */
public interface Master<O, R, V> extends Job<V> {

    /**
     * Split a job (any {@link com.elasticgrid.Task} actually) into a suite of tasks to be executed.
     * @param args the args for which tasks should be populated
     * @return the list of {@link com.elasticgrid.Task}s to execute
     */
    List<Task> map(O args);

    /**
     * Collect the results of {@link com.elasticgrid.Task}s executions from the {@link #map} method.
     * @param results the list of results
     * @throws RemoteException if there is a network failure
     * @return the reduced...
     */
    V reduce(List<R> results);

}
