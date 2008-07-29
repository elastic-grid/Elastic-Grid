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

package com.elasticgrid.amazon.ec2;

import com.elasticgrid.grid.GridLocator;
import com.elasticgrid.model.GridException;
import com.elasticgrid.model.GridNotFoundException;
import com.elasticgrid.model.ec2.EC2Node;
import java.util.List;

public interface EC2GridLocator extends GridLocator<EC2Node> {

    /**
     * Locate all grids.
     * @return the grids name.
     * @throws GridException if tehre is a technical error
     */
    List<String> findGrids() throws GridException;

    /**
     * Locate nodes which are part of a grid.
     * @param gridName the name of the grid for whom nodes should be found
     * @return the list of {@link EC2Node}s
     * @throws GridNotFoundException if the grid can't be found
     * @throws GridException if there is a technical error
     */
    List<EC2Node> findNodes(String gridName) throws GridNotFoundException, GridException;
}
