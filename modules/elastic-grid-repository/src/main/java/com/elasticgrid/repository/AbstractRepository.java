package com.elasticgrid.repository;

import com.elasticgrid.model.Application;
import com.elasticgrid.model.Grid;
import com.elasticgrid.model.internal.AbstractStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Repository implementation helper class.
 * @author Jerome Bernard
 */
public abstract class AbstractRepository implements Repository {
	private Map<String, Grid> grids = Collections.synchronizedMap(new HashMap<String, Grid>());
    private Map<String, Application> applications = Collections.synchronizedMap(new HashMap<String, Application>());
    protected AbstractStore store;
    protected boolean initialized = false;
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected abstract Logger getLogger();

    public AbstractRepository grid(String name, Grid grid) {
        grids.put(name, grid);
        return this;
    }

    public Map<String, Grid> getGrids() throws RemoteException {
        return grids;
    }

	public AbstractRepository application(Application application) throws RemoteException {
        if (!initialized) {
            logger.warn("Remote repository has not been bootstrapped. Bootstrapping it right now...");
            bootstrap();
        }
        store.application(application.getName());
        getApplications().put(application.getName(), application);
        return this;
    }

    public Map<String, Application> getApplications() throws RemoteException {
        return applications;
    }

	public Repository delete(Application application) throws RemoteException {
        store.getApplications().remove(application.getName());
        getApplications().remove(application.getName());
        return this;
    }

    protected File getRoot() {
        return new File(getEgHome());
    }

    protected File getLocalRepository() {
        return new File(getEgHome(), LOCAL_DIRECTORY);
    }

    protected File getLocalApplicationsDirectory() {
        File directory = new File(getLocalRepository(), APPLICATIONS_DIRECTORY);
        if (!directory.exists())
            directory.mkdir();
        return directory;
    }

    protected File getLocalConfigurationDirectory() {
        File directory = new File(getLocalRepository(), CONFIGURATION_DIRECTORY);
        if (!directory.exists())
            directory.mkdir();
        return directory;
    }

    private String getEgHome() {
        return System.getProperty("user.home") + File.separatorChar + ".eg";
    }

}
