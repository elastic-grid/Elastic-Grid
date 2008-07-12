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

import com.elasticgrid.JobStatus;
import com.elasticgrid.Task;

/**
 * @author Jerome Bernard
 */
public class JobStatusImpl implements JobStatus {
    private final long numberOfTasks;
    private long completedTasks;
    private long pendingTasks;

    public JobStatusImpl(final int numberOfTasks, final int completedTasks, final int pendingTasks) {
        this.numberOfTasks = numberOfTasks;
        this.completedTasks = completedTasks;
        this.pendingTasks = pendingTasks;
    }

    public long getNumberOfTasks() {
        return numberOfTasks;
    }

    public long getCompletedTasks() {
        return completedTasks;
    }

    public synchronized void taskCompleted() {
        completedTasks++;
        pendingTasks--;
    }

    public long getPendingTasks() {
        return pendingTasks;
    }

    public double getProgress() {
        return ((double) completedTasks) / numberOfTasks;
    }
}
