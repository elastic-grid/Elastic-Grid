/**
 * Elastic Grid
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
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

package com.elasticgrid.space;

import net.jini.core.entry.Entry;
import com.elasticgrid.Task;

import java.io.Serializable;

/**
 * 
 * @author Jerome Bernard.
 */
public class TaskEntry<V extends Serializable> implements Entry {
    public String taskID;
    public Task<V> task;

    public TaskEntry() {
    }

    public TaskEntry(String taskID, Task<V> task) {
        this.taskID = taskID;
        this.task = task;
    }

    public String getTaskID() {
        return taskID;
    }

    public Task<V> getTask() {
        return task;
    }
}
