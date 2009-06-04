/**
 * Elastic Grid
 * Copyright (C) 2008-2009 Elastic Grid, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.elasticgrid.repository;

import com.elasticgrid.model.Application;
import com.elasticgrid.model.Grid;
import com.elasticgrid.model.internal.AbstractStore;
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
    protected Logger logger = Logger.getLogger(getClass());

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
