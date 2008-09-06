/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This file is part of Elastic Grid.
 * 
 * Elastic Grid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * Elastic Grid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Elastic Grid.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elasticgrid.repository.impl;

import com.elasticgrid.model.Application;
import com.elasticgrid.model.Grid;
import com.elasticgrid.model.GridFactory;
import com.elasticgrid.model.internal.ApplicationImpl;
import com.elasticgrid.repository.LocalRepository;
import com.elasticgrid.repository.RemoteRepository;
import com.elasticgrid.repository.RepositoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Repository manager implementation.
 * @author Jerome Bernard
 */
public class RepositoryManagerImpl implements RepositoryManager {
    private LocalRepository localRepository;
    private RemoteRepository remoteRepository;
    private GridFactory gridFactory;
    private Map<String, Grid> grids = Collections.synchronizedMap(new HashMap<String, Grid>());
    private Map<String, Application> applications = Collections.synchronizedMap(new HashMap<String, Application>());
    private Logger logger = LoggerFactory.getLogger(RepositoryManager.class);

    public void bootstrap() throws RemoteException {
        localRepository.bootstrap();
        remoteRepository.bootstrap();
        restore();
        localRepository.load();
        remoteRepository.load();
        grids.putAll(remoteRepository.getGrids());
        applications.putAll(remoteRepository.getApplications());
        applications.putAll(localRepository.getApplications());
    }

    public void restore() throws RemoteException {
        remoteRepository.restore();
    }

    @SuppressWarnings("unchecked")
    public void upload() throws RemoteException {
        logger.info("Uploading local repository content to Amazon S3 remote directory...");
        try {
            Collection<File> files = localRepository.listFiles();
            for (File file : files) {
                remoteRepository.upload(file);
            }
        } catch (Exception e) {
            throw new RemoteException("Unexpected error when uploading content to remote repository", e);
        }
    }

    public Map<String, Grid> getGrids() throws RemoteException {
        return grids;
    }

    public Grid grid(String name) throws RemoteException {
        // return the existing grid if it exists
        if (grids.containsKey(name))
            return grids.get(name);
        Grid grid = gridFactory.createGrid().name(name);
        grids.put(name, grid);
        return grid;
    }

    public void save(Grid grid) throws RemoteException {
        // save the grid in the local repository
        localRepository.getGrids().put(grid.getName(), grid);
        localRepository.save();

        // save the grid in the remote repository
        remoteRepository.getGrids().put(grid.getName(), grid);
        remoteRepository.save();
    }

    public void destroyGrid(String name) throws RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void describeGrid(String name) throws RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void resizeGrid(String name, int size) throws RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Map<String, Application> getApplications() throws RemoteException {
        return applications;
    }

    public Application application(String name) throws RemoteException {
        // return the existing application if it exists
        if (applications.containsKey(name))
            return applications.get(name);
        Application application = new ApplicationImpl().name(name);
        applications.put(name, application);
        return application;
    }

    public void save(Application application) throws RemoteException {
        localRepository.getApplications().put(application.getName(), application);
        localRepository.save();
    }

    public void destroyApplication(String name) throws RemoteException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void publishApplication(Application application) throws RemoteException {
        remoteRepository.application(application);
        remoteRepository.save();
    }

    public void unpublishApplication(Application application) throws RemoteException {
        remoteRepository.delete(application);
        remoteRepository.save();
    }

    public void setLocalRepository(LocalRepository localRepository) {
        this.localRepository = localRepository;
    }

    public void setRemoteRepository(RemoteRepository remoteRepository) {
        this.remoteRepository = remoteRepository;
    }

    public void setGridFactory(GridFactory gridFactory) {
        this.gridFactory = gridFactory;
    }
}
