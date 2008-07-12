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
