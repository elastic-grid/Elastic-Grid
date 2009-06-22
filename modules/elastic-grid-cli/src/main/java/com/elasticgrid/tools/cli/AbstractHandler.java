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

package com.elasticgrid.tools.cli;

import com.elasticgrid.cluster.ClusterManager;
import com.elasticgrid.storage.StorageManager;
import org.rioproject.tools.cli.OptionHandler;
import java.io.IOException;
import net.jini.config.ConfigurationException;

abstract class AbstractHandler implements OptionHandler {
    protected ClusterManager getClusterManager() throws IOException {
        return CLI.getClusterManager();
    }

    protected StorageManager getStorageManager() throws ConfigurationException {
        return (StorageManager) CLI.getInstance().getConfiguration().getEntry(CLI.COMPONENT, "", StorageManager.class);
    }
}
