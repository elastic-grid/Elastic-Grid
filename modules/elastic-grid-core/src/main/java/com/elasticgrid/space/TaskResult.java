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

import net.jini.core.entry.Entry;

import java.io.Serializable;

/**
 * @author Jerome Bernard
 */
public class TaskResult<V extends Serializable> implements Entry {
    public String taskID;
    public V result;

    public TaskResult() {
    }

    public TaskResult(String taskID) {
        this.taskID = taskID;
    }

    public TaskResult(String taskID, V result) {
        this.taskID = taskID;
        this.result = result;
    }

    public String getTaskID() {
        return taskID;
    }

    public V getResult() {
        return result;
    }
}
