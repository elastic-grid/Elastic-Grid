/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This file is part of Elastic Grid.
 * 
 * Elastic Grid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * 
 * Elastic Grid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Elastic Grid.  If not, see <http://www.gnu.org/licenses/>.
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
