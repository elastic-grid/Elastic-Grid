package com.elasticgrid.repository;

import com.elasticgrid.model.Application;

import java.rmi.RemoteException;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Remote repository.
 * @author Jerome Bernard
 */
public interface RemoteRepository extends Repository {

	/**
     * Restore the local file-system from the content of the remote repository.
     * @throws RemoteException if there is a network failure
     */
    void restore() throws RemoteException;

    /**
     * Upload a local file to the remote repository.
     * @param file the local file to upload
     * @return the URL of the uploaded file
     * @throws FileNotFoundException if the local file can't be found
     * @throws RemoteException if there is a network failure
     */
    String upload(File file) throws RemoteException, FileNotFoundException;

}
