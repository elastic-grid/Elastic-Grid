package com.elasticgrid.tools.cli.app;

import java.util.List;
import java.rmi.RemoteException;

/**
 * Destroy a grid application.
 * @author Jerome Bernard
 */
public class DestroyApplicationCommand extends AbstractApplicationCommand {
    void execute(String applicationName, List<String> args) throws RemoteException {
        repositoryManager.destroyApplication(applicationName);
    }
}
