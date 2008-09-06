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

package com.elasticgrid.tools.cli;

import com.elasticgrid.model.GridNotFoundException;
import org.rioproject.tools.cli.OptionHandler;
import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.StringTokenizer;

public class StopGridHandler extends AbstractHandler implements OptionHandler {

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
            // first token is "stop-grid"
            tok.nextToken();
            // second token is grid name
            String gridName = tok.nextToken();
            try {
                getGridManager().stopGrid(gridName);
                return "Grid '" + gridName + "' stopped";
            } catch (GridNotFoundException e) {
                return "grid not found!";
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
        return("usage: stop-grid gridName\n");
    }

}