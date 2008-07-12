package com.elasticgrid.tools.cli;

import com.elasticgrid.repository.Repository;
import com.elasticgrid.repository.RepositoryManager;

import java.rmi.RemoteException;

/**
 * Synchronize local file-system with remote applications repository.
 * @see Repository#synchronize()
 * @author Jerome Bernard
 */
public class SynchronizeCommand extends AbstractCommand {
    private RepositoryManager repositoryManager;

    public void execute(String... args) throws IllegalArgumentException, RemoteException {
        //repositoryManager.synchronize();`
        throw new UnsupportedOperationException();
    }

    public void setRepositoryManager(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }
}
