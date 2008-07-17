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

package com.elasticgrid.tools.cli.old.grid;

import com.elasticgrid.model.GridNotFoundException;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import java.rmi.RemoteException;
import java.util.List;
import static java.lang.String.format;

/**
 * Resize the grid so that it shrinks or grows to the specified size.
 * @author Jerome Bernard
 */
public class ResizeGridCommand extends AbstractGridCommand {
    @Argument(index = 0, usage = "New size of the grid", required = true)
    private int newSize;

    void execute(String gridName, List<String> args) throws RemoteException {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args.toArray(new String[args.size()]));
        } catch (CmdLineException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        try {
            gridManager.resizeGrid(gridName, newSize);
        } catch (GridNotFoundException e) {
            logger.error(format("Can't find grid '%s'", gridName));
        }
    }
}
