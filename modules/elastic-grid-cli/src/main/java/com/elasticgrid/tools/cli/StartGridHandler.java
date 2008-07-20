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

package com.elasticgrid.tools.cli;

import org.rioproject.tools.cli.OptionHandler;
import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.StringTokenizer;
import com.elasticgrid.model.GridAlreadyRunningException;

public class StartGridHandler extends AbstractHandler implements OptionHandler {

    /**
     * Process the option.
     *
     * @param input Parameters for the option, may be null
     * @param br An optional BufferdReader, used if the option requires input.
     * if this is null, the option handler may create a BufferedReader to handle the input
     * @param out The PrintStream to use if the option prints results or
     * choices for the user. Must not be null
     *
     * @return The result of the action.
     */
    public String process(String input, BufferedReader br, PrintStream out) {
        StringTokenizer tok = new StringTokenizer(input);
        if (tok.countTokens() > 1) {
            // first token is "start-grid"
            tok.nextToken();
            String gridName = tok.nextToken();
            int gridSize = 1;
            if (tok.countTokens() == 3)
                gridSize = Integer.parseInt(tok.nextToken());
            try {
                getGridManager().startGrid(gridName, gridSize);
                return "Grid started with " + gridSize + " node(s)";
            } catch (GridAlreadyRunningException e) {
                return "grid already running!";
            } catch (Exception e) {
                e.printStackTrace(out);
                return "unexpected grid exception";
            }
        } else {
            return getUsage();
        }
    }

    /**
     * Get the usage of the command
     *
     * @return Command usage
     */
    public String getUsage() {
        return("usage: start-grid gridName [-s gridSize]\n");
    }

}
