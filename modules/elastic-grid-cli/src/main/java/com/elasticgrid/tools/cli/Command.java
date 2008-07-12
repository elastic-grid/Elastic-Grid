package com.elasticgrid.tools.cli;

import java.rmi.RemoteException;

/**
 * @author Jerome Bernard
 */
public interface Command {
    void execute(String... args) throws IllegalArgumentException, RemoteException;
}
