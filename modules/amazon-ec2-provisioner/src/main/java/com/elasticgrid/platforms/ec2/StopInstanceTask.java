package com.elasticgrid.platforms.ec2;

import java.util.concurrent.Callable;
import java.rmi.RemoteException;

public class StopInstanceTask implements Callable<Void> {
    private EC2Instantiator nodeInstantiator;
    private String instanceID;

    public StopInstanceTask(EC2Instantiator nodeInstantiator, String instanceID) {
        this.nodeInstantiator = nodeInstantiator;
        this.instanceID = instanceID;
    }

    public Void call() throws RemoteException {
        nodeInstantiator.shutdownInstance(instanceID);
        return null;
    }
}
