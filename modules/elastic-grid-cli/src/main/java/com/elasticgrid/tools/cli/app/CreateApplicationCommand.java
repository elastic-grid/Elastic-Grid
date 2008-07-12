package com.elasticgrid.tools.cli.app;

import com.elasticgrid.model.Application;

import java.util.List;
import java.rmi.RemoteException;

/**
 * Create a grid application.
 * @author Jerome Bernard
 */
public class CreateApplicationCommand extends AbstractApplicationCommand {
    void execute(String applicationName, List<String> args) throws RemoteException {
        Application application = repositoryManager.application(applicationName);
        // todo: complete the creation of the application
        // save the application to the local repository
        repositoryManager.save(application);
    }
}
