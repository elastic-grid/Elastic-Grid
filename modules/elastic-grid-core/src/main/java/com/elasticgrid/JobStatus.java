/**
 * Elastic Grid
 * Copyright (C) 2008-2009 Elastic Grid, LLC.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elasticgrid;

import java.io.Serializable;

/**
 * @author Jerome Bernard
 */
public interface JobStatus extends Serializable {
    long getNumberOfTasks();
    long getCompletedTasks();
    void taskCompleted();
    long getPendingTasks();
    double getProgress();

    public JobStatus ZERO = new JobStatus() {
        public long getNumberOfTasks() { return 0; }
        public long getCompletedTasks() { return 0; }
        public void taskCompleted() { }
        public long getPendingTasks() { return 0; }
        public double getProgress() { return 0; }
    };
}
