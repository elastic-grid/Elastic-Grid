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

package com.elasticgrid.space;

import com.elasticgrid.Job;
import com.elasticgrid.JobStatus;
import com.elasticgrid.Task;
import net.jini.id.UuidFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Jerome Bernard
 */
public abstract class AbstractJob<V> implements Job<V> {
    private String jobID;
    private List<Task> tasks;
    private JobStatus status;
    private FutureTask<V> future;

    public AbstractJob() {
        this.jobID = UuidFactory.generate().toString();
        this.future = new FutureTask<V>(this);
        this.tasks = new ArrayList<Task>();
    }

    public synchronized void addTasks(List<Task> newTasks) {
        tasks.addAll(newTasks);
        status = new JobStatusImpl(tasks.size(), 0, tasks.size());
    }

    public String getJobID() {
        return jobID;
    }

    public JobStatus getStatus() {
        if (status == null)
            return JobStatus.ZERO;
        else
            return status;
    }

    public boolean isCancelled() {
        return future.isCancelled();
    }

    public boolean isDone() {
        return future.isDone();
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    public V get() throws InterruptedException, ExecutionException {
        return future.get();
    }

    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(timeout, unit);
    }

    public void run() {
        future.run();
    }
}
