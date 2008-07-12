package com.elasticgrid.tools.cli.app;

import com.elasticgrid.tools.cli.AbstractCommand;
import com.elasticgrid.repository.RepositoryManager;

import java.util.Arrays;
import java.util.List;
import java.rmi.RemoteException;

/**
 * @author Jerome Bernard
 */
public abstract class AbstractApplicationCommand extends AbstractCommand {
    protected RepositoryManager repositoryManager;

    abstract void execute(String applicationName, List<String> args) throws RemoteException;

    public void execute(String... args) throws RemoteException {
        String applicationName = args[0];
        execute(applicationName, Arrays.asList(args).subList(1, args.length));
    }

    public void setRepositoryManager(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }
}