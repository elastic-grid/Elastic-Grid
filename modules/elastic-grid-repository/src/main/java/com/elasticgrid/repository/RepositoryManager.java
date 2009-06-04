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
package com.elasticgrid.repository;

import com.elasticgrid.model.Grid;
import com.elasticgrid.model.Application;

import java.rmi.RemoteException;
import java.util.Map;

/**
 * @author Jerome Bernard
 */
public interface RepositoryManager<G extends Grid> {

    /**
     * Bootstrap both the local and remote repositories.
     * @throws RemoteException if there is a network failure
     */
    void bootstrap() throws RemoteException;

    /**
     * Restore the local repository from the content of the remote repository.
     * @throws RemoteException if there is a network failure
     */
    void restore() throws RemoteException;

    /**
     * Get the grids.
     * Note: the key of the map is the name of the grid.
     * @return the map of grids.
     * @throws RemoteException if there is a network failure
     */
    Map<String, G> getGrids() throws RemoteException;

    /**
     * Create a new grid in memory. The created grid is NOT SAVED!
     * @param name the name of the grid to create
     * @return a grid stub to be filled in
     * @throws RemoteException if there is a network failure
     */
    G grid(String name) throws RemoteException;

    /**
     * Save the grid in both the local and remote repositories.
     * @param grid the grid to create
     * @throws RemoteException if there is a network failure
     */
    void save(G grid) throws RemoteException;

    /**
     * Destroy a grid with the specified name.
     * Undeploy all its running applications first.
     * @param name the name of the grid to destroy
     * @throws RemoteException if there is a network failure
     */
    void destroyGrid(String name) throws RemoteException;

	/**
     * Describe a grid with the specified name.
     * @param name the name of the grid to describe
     * @throws RemoteException if there is a network failure
     */
    void describeGrid(String name) throws RemoteException;

	/**
     * Resize a grid with the specified name to the specified size of nodes.
     * @param name the name of the grid to resize
     * @param size the number of nodes which should be part of the grid
     * @throws RemoteException if there is a network failure
     */
    void resizeGrid(String name, int size) throws RemoteException;

    /**
     * Get the applications.
     * Note: the key of the map is the name of the application.
     * @return the map of applications.
     * @throws RemoteException if there is a network failure
     */
    Map<String, Application> getApplications() throws RemoteException;

    /**
     * Create a new application in memory. The created application is NOT SAVED!
     * @param name the name of the application to create
     * @return an application stub to be filled in
     * @throws RemoteException if there is a network failure
     * @see #save(Application)
     */
    Application application(String name) throws RemoteException;

    /**
     * Save the application in local repository.
     * @param application the application to be saved
     * @throws RemoteException if there is a network failure
     */
    void save(Application application) throws RemoteException;

    /**
     * Destroy the specified application from both local and remote repositories.
     * @param name the name of the application to destroy
     * @throws RemoteException if there is a network failure
     */
    void destroyApplication(String name) throws RemoteException;

	/**
     * Publish a local application to the remote repository.
     * @param application the application to publish
     * @throws RemoteException if there is a network failure
     */
    void publishApplication(Application application) throws RemoteException;

    /**
     * Unpublish an application from the remote repository.
     * @param application the application to unpublish
     * @throws RemoteException if there is a network failure
     */
    void unpublishApplication(Application application) throws RemoteException;

}
