/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This file is part of Elastic Grid.
 * 
 * Elastic Grid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * Elastic Grid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Elastic Grid.  If not, see <http://www.gnu.org/licenses/>.
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
