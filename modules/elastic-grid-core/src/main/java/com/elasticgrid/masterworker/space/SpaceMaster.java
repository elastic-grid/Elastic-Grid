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

package com.elasticgrid.masterworker.space;

import com.elasticgrid.masterworker.Master;
import com.elasticgrid.space.TaskEntry;
import com.elasticgrid.space.TaskResult;
import com.elasticgrid.space.AbstractJob;
import com.elasticgrid.Task;
import com.elasticgrid.JobStatus;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace05;
import net.jini.config.ConfigurationException;
import org.springframework.util.Assert;
import org.rioproject.watch.PeriodicWatch;
import org.rioproject.watch.Calculable;
import org.rioproject.watch.GaugeWatch;
import org.rioproject.watch.WatchRegistry;
import org.rioproject.core.jsb.ServiceBeanContext;

import static java.lang.String.format;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.Serializable;

/**
 * Abstract {@link com.elasticgrid.masterworker.Master} based on JavaSpaces.
 * @author Jerome Bernard
 */
public abstract class SpaceMaster<O, R extends Serializable, V extends Serializable> extends AbstractJob<V> implements Master<O, R, V>, Callable<V> {
    private O args;
    private JavaSpace05 space;
    private long txnTimeout = 2000, pollTimeout = 2000, processTimeout = 20000;
    protected final Logger logger = Logger.getLogger(getClass().getName());
    private static final long TIME_OUT = 30 * 60 * 1000;

    protected SpaceMaster(O args, JavaSpace05 space, ServiceBeanContext context) throws ConfigurationException, RemoteException {
        super();
        Assert.notNull(space, "The JavaSpace proxy should be given!");
        this.args = args;
        this.space = space;
        Long watchPeriod = (Long) context.getConfiguration().getEntry(
                "com.elasticgrid.space.SpaceMaster", "watchPeriod", Long.class, 5000L);
        WatchRegistry watchRegistry = context.getWatchRegistry();
        if (watchRegistry.findWatch("Completed Tasks").length == 0) {
            PeriodicWatch completedTasksWatch = new PeriodicWatch("Completed Tasks") {
                public void checkValue() {
                    addWatchRecord(new Calculable(id, getStatus().getCompletedTasks(), System.currentTimeMillis()));
                }
            };
            completedTasksWatch.setPeriod(watchPeriod);
            watchRegistry.register(completedTasksWatch);
        }
        if (context.getWatchRegistry().findWatch("Pending Tasks").length == 0) {
            PeriodicWatch pendingTasksWatch = new PeriodicWatch("Pending Tasks") {
                public void checkValue() {
                    addWatchRecord(new Calculable(id, getStatus().getPendingTasks(), System.currentTimeMillis()));
                }
            };
            pendingTasksWatch.setPeriod(watchPeriod);
            watchRegistry.register(pendingTasksWatch);
        }
        if (watchRegistry.findWatch("Job Progress").length == 0) {
            PeriodicWatch progressWatch = new PeriodicWatch("Job Progress") {
                public void checkValue() {
                    addWatchRecord(new Calculable(id, getStatus().getProgress(), System.currentTimeMillis()));
                }
            };
            progressWatch.setPeriod(watchPeriod);
            watchRegistry.register(progressWatch);
        }
    }

    public List<Task> getTasks() {
        return map(args);
    }

    public V call() throws Exception {
        logger.info("Splitting task...");
        List<Task> tasks = map(args);
        addTasks(tasks);
        logger.info("Processing tasks...");
        List<R> results = processTasks(tasks);
        logger.info("Collecting results...");
        return reduce(results);
    }

    public List<R> processTasks(List<? extends Task> tasks) throws RemoteException, LeaseDeniedException, TransactionException, UnusableEntryException, InterruptedException {
        // write requests
        List<TaskEntry<R>> entries = new ArrayList<TaskEntry<R>>(tasks.size());
        List<Long> leasesDurations = new ArrayList<Long>(tasks.size());
        for (Task<R> task : tasks) {
            TaskEntry<R> entry = new TaskEntry<R>(task.getJobID(), task);
            logger.info(format("Writing task entry %s", entry.getTaskID()));
            entries.add(entry);
            leasesDurations.add(TIME_OUT);
        }
        space.write(entries, null, leasesDurations);
        logger.info(format("Wrote %d tasks", entries.size()));
        // retrieve results
        List<R> results = new LinkedList<R>();
        long start = System.currentTimeMillis();
        for (TaskEntry<R> entry : entries) {
            logger.info(format("Searching for task results for task %s", entry.getTaskID()));
            Entry template = space.snapshot(new TaskResult<V>(entry.getTaskID()));
            while ((System.currentTimeMillis() - start <= pollTimeout * 10) && (results.size() < entries.size())) {
                TaskResult<R> result = (TaskResult<R>) space.takeIfExists(template, null, pollTimeout);
                if (result == null) {
                    Thread.sleep(100);
                    continue;
                }
                results.add(result.getResult());
                getStatus().taskCompleted();
            }
        }
        if (results.size() != entries.size()) {
            logger.log(Level.SEVERE,
                    format("Could not complete job in the allocated time slot (%d ms). Expected %d results but only got %d.",
                            processTimeout, entries.size(), results.size()));
            return null;
        }
        long end = System.currentTimeMillis();
        logger.info(format("Processing of master task took %d milliseconds", end - start));
        return results;
    }
}
