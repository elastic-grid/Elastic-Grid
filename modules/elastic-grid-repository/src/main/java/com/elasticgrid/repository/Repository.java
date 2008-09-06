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

package com.elasticgrid.repository;

import com.elasticgrid.model.Grid;
import com.elasticgrid.model.Application;

import java.rmi.RemoteException;
import java.util.Map;
import java.io.IOException;

/**
 * Elastic Grid remote repository.
 * @author Jerome Bernard
 */
public interface Repository {
    /**
     * Directory relative to <tt>$EG_HOME</tt> where the local copy of the remote repository is located.
     */
    String LOCAL_DIRECTORY = "repository";

    /**
     * Directory relative to the root of the repository into which the applications are stored.
     */
    String APPLICATIONS_DIRECTORY = "applications";

    /**
     * Directory relative to the root of the repository into which the configuration files are stored.
     */
    String CONFIGURATION_DIRECTORY = "config";

    /**
     * Initialize both the local and remote repositories.
     * @throws RemoteException if there is a network failure
     */
    void bootstrap() throws RemoteException;

    /**
     * Load the content of the repository into memory.
     * @throws RemoteException if there is a network failure
     */
    void load() throws RemoteException;

    /**
     * Save the content of the repository into persistent storage area.
     * @throws RemoteException if there is a network failure
     */
    void save() throws RemoteException;

    /**
     * Purge the remote repository.
     * @throws IOException if there is an I/O error
     */
    void purge() throws IOException;

    /**
     * Get the grids.
     * Note: the key of the map is the name of the grid.
     * @return the map of grids.
     * @throws RemoteException if there is a network failure
     */
    Map<String, Grid> getGrids() throws RemoteException;

    Repository application(Application application) throws RemoteException;

    Repository delete(Application application) throws RemoteException;

    /**
     * Get the applications.
     * Note: the key of the map is the name of the application.
     * @return the map of applications.
     * @throws RemoteException if there is a network failure
     */
    Map<String, Application> getApplications() throws RemoteException;
    
}
