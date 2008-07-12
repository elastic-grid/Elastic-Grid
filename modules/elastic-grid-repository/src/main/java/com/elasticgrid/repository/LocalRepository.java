package com.elasticgrid.repository;

import java.rmi.RemoteException;
import java.io.File;
import java.util.Collection;

/**
 * Local repository.
 * @author Jerome Bernard
 */
public interface LocalRepository extends Repository {

    File getRoot();

    /**
     * List the file available in the local repository.
     * @return the list of files
     */
    Collection<File> listFiles();
	
}
