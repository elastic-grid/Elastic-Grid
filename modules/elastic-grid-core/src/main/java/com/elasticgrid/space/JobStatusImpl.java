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

package com.elasticgrid.space;

import com.elasticgrid.JobStatus;

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

    public synchronized long getNumberOfTasks() {
        return numberOfTasks;
    }

    public synchronized long getCompletedTasks() {
        return completedTasks;
    }

    public synchronized void taskCompleted() {
        completedTasks++;
        pendingTasks--;
    }

    public synchronized long getPendingTasks() {
        return pendingTasks;
    }

    public synchronized double getProgress() {
        return ((double) completedTasks) / numberOfTasks;
    }
}
