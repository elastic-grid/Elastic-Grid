package com.elasticgrid.tools.cli.app;

import java.util.List;
import java.rmi.RemoteException;

/**
 * Publish an application on the remote repository.
 * @author Jerome Bernard
 */
public class PublishApplicationCommand extends AbstractApplicationCommand {
    void execute(String applicationName, List<String> args) throws RemoteException {
        // todo: repositoryManager.publishApplication(applicationName);
    }
}
