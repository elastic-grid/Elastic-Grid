package com.elasticgrid.grid.ec2;

import com.elasticgrid.grid.GridManager;
import com.elasticgrid.model.Grid;
import com.elasticgrid.model.ec2.EC2Grid;
import com.elasticgrid.model.ec2.EC2Node;
import com.elasticgrid.repository.RepositoryManager;
import com.elasticgrid.amazon.ec2.EC2Instantiator;
import com.elasticgrid.amazon.ec2.InstanceType;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.rmi.RemoteException;
import static java.lang.String.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EC2GridManager implements GridManager<EC2Grid> {
    private RepositoryManager<EC2Grid> repositoryManager;
    private EC2Instantiator ec2;
    private String keyName;
    private String amiSmall, amiLarge, amiExtraLarge;
    private static final Logger logger = LoggerFactory.getLogger(EC2GridManager.class);

    public void startGrid(String gridName) throws GridAlreadyRunningException, RemoteException {
        startGrid(gridName, 1);
    }

    public void startGrid(String gridName, int size) throws GridAlreadyRunningException, RemoteException {
        // ensure the grid is not already running
        Grid grid = grid(gridName);
        if (grid != null && grid.isRunning()) {
            throw new GridAlreadyRunningException(grid);
        }
        // todo: start the grid accordingly to the parameters
        InstanceType instanceType = InstanceType.SMALL;
        String ami = null;
        switch (instanceType) {
            case SMALL:
                ami = amiSmall;
                break;
            case LARGE:
                ami = amiLarge;
                break;
            case EXTRA_LARGE:
                ami = amiExtraLarge;
                break;
            default:
                throw new IllegalArgumentException(format("Unexpected Amazon EC2 instance type '%s'", instanceType.getName()));
        }
        List<String> groupSet = new ArrayList<String>();
        String userData = "";
        List<String> instances = ec2.startInstances(ami, size, size, groupSet, userData, keyName, true, instanceType);
    }

    public void stopGrid(String gridName) throws GridNotFoundException, RemoteException {
        stopGrid(grid(gridName));
    }

    public EC2Grid stopGrid(EC2Grid grid) throws RemoteException {
        Set<EC2Node> nodes = grid.getNodes();
        for (EC2Node node : nodes) {
            String instanceID = node.getInstanceID();
            logger.info("Shutting down Amazon EC2 instance '{}'", instanceID);
            ec2.shutdownInstance(instanceID);
        }
        grid.status(Grid.Status.STOPPED);
        return grid;
    }

    public EC2Grid grid(String name) throws RemoteException {
        return repositoryManager.grid(name);
    }

    public void resizeGrid(String gridName, int newSize) throws GridNotFoundException, RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void destroyGrid(String gridName) throws GridNotFoundException, RemoteException {
        // locate the grid
        EC2Grid grid = repositoryManager.grid(gridName);
        if (grid == null)
            throw new GridNotFoundException(gridName);
        // stop it if running
        if (grid.isRunning()) {
            stopGrid(grid);
        }
        // destroy it from the repository
        repositoryManager.destroyGrid(grid.getName());
    }

    public void setRepositoryManager(RepositoryManager<EC2Grid> repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    public void setEc2(EC2Instantiator ec2) {
        this.ec2 = ec2;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public void setAmiSmall(String amiSmall) {
        this.amiSmall = amiSmall;
    }

    public void setAmiLarge(String amiLarge) {
        this.amiLarge = amiLarge;
    }

    public void setAmiExtraLarge(String amiExtraLarge) {
        this.amiExtraLarge = amiExtraLarge;
    }
}
