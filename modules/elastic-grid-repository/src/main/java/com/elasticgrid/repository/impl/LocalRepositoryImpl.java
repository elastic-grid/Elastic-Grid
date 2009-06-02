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
import com.elasticgrid.model.ec2.impl.EC2GridImpl;
import com.elasticgrid.model.internal.ApplicationImpl;
import com.elasticgrid.model.internal.LocalStore;
import com.elasticgrid.repository.AbstractRepository;
import com.elasticgrid.repository.LocalRepository;
import com.elasticgrid.utils.jibx.ObjectXmlMappingException;
import com.elasticgrid.utils.jibx.XmlUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.String.format;
import java.rmi.RemoteException;
import java.util.Collection;

/**
 * Local repository implementation.
 *
 * @author Jerome Bernard
 */
public class LocalRepositoryImpl extends AbstractRepository implements LocalRepository {
    private File configuration;
    private boolean initialized = false;
    private static final String GRID_FILE_NAME_FORMAT = "grid-%s.xml";

    public void bootstrap() throws RemoteException {
        // create the local repository if needed
        if (getLocalRepository().mkdir())
            logger.info(format("Creating local repository in %s", getLocalRepository()));
        // create the global configuration file if needed
        configuration = new File(getLocalConfigurationDirectory(), "elastic-grid.xml");
        // create an empty store
        store = new LocalStore();
        // mark repository as initialized
        initialized = true;
    }

    public void load() throws RemoteException {
        if (!initialized) {
            logger.warn("Local repository has not been bootstrapped. Bootstrapping it right now...");
            bootstrap();
        }
        // load the global configuration file
        if (!configuration.exists()) {
            try {
                configuration.createNewFile();
                save();
                return;
            } catch (IOException e) {
                throw new RemoteException(format("Can't create file '%s'", configuration.getAbsolutePath()), e);
            }
        }
        FileReader reader = null;
        try {
            reader = new FileReader(configuration);
            store = XmlUtils.convertXmlToObject("ElasticGrid", LocalStore.class, reader);
            // load the grids
            loadGrids();
            // load the applications
            loadApplications();
        } catch (FileNotFoundException e) {
            throw new RemoteException(format("Can't find the XML configuration file '%s'",
                    configuration.getAbsolutePath()), e);
        } catch (ObjectXmlMappingException e) {
            throw new RemoteException(format("Can't parse the XML configuration file '%s'",
                    configuration.getAbsolutePath()), e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    public void save() throws RemoteException {
        // save the global configuration file
        try {
            for (Grid grid : getGrids().values())
                store.grid(grid.getName());
            for (Application app : getApplications().values())
                store.application(app.getName());
            String data = XmlUtils.convertObjectToXml("ElasticGrid", store, "UTF-8");
            logger.debug("Saving data into '{}':\n{}", configuration.getAbsolutePath(), data);
            FileUtils.writeStringToFile(configuration, data, "UTF-8");
        } catch (Exception e) {
            throw new RemoteException(format("Can't write repository configuration into file '%s'",
                    configuration.getAbsolutePath()), e);
        }
        // save the grids
        saveGrids();
        // save the applications
        saveApplications();
    }

    private void loadGrids() throws ObjectXmlMappingException, RemoteException {
        for (String gridName : store.getGrids()) {
            File gridConfiguration = new File(getLocalConfigurationDirectory(), format(GRID_FILE_NAME_FORMAT, gridName));
            FileReader gridReader = null;
            try {
                gridReader = new FileReader(gridConfiguration);
                Grid grid = XmlUtils.convertXmlToObject("ElasticGrid", EC2GridImpl.class, gridReader);
                grid(gridName, grid);
            } catch (FileNotFoundException e) {
                throw new RemoteException(format("Can't find the XML configuration file '%s'",
                        gridConfiguration.getAbsolutePath()), e);
            } finally {
                IOUtils.closeQuietly(gridReader);
            }
        }
    }

    private void saveGrids() throws RemoteException {
        File gridFile = null;
        try {
            logger.info("Saving {} grids", getGrids().size());
            for (Grid grid : getGrids().values()) {
                gridFile = new File(getLocalConfigurationDirectory(), format(GRID_FILE_NAME_FORMAT, grid.getName()));
                String data = XmlUtils.convertObjectToXml("ElasticGrid", grid, "UTF-8");
                logger.debug("Saving data into '{}':\n{}", gridFile.getAbsolutePath(), data);
                FileUtils.writeStringToFile(gridFile, data, "UTF-8");
            }
        } catch (Exception e) {
            throw new RemoteException(format("Can't write grid configuration into file '%s'",
                    gridFile != null ? gridFile.getAbsolutePath() : ""), e);
        }
    }

    private void loadApplications() throws ObjectXmlMappingException, RemoteException {
        for (String applicationName : store.getApplications()) {
            File appConfiguration = new File(getApplicationDirectory(applicationName), "application.xml");
            FileReader appReader = null;
            try {
                appReader = new FileReader(appConfiguration);
                application(XmlUtils.convertXmlToObject("ElasticGrid", ApplicationImpl.class, appReader));                
            } catch (FileNotFoundException e) {
                throw new RemoteException(format("Can't find the XML configuration file '%s'",
                        appConfiguration.getAbsolutePath()), e);
            } finally {
                IOUtils.closeQuietly(appReader);
            }
        }
    }

    private void saveApplications() throws RemoteException {
        File appFile = null;
        try {
            logger.info("Saving {} applications...", getApplications().size());
            for (Application application : getApplications().values()) {
                appFile = new File(getApplicationDirectory(application.getName()), "application.xml");
                String data = XmlUtils.convertObjectToXml("ElasticGrid", application, "UTF-8");
                logger.debug("Saving data into '{}':\n{}", appFile.getAbsolutePath(), data);
                FileUtils.writeStringToFile(appFile, data, "UTF-8");
            }
        } catch (Exception e) {
            throw new RemoteException(format("Can't write application configuration into file '%s'",
                    appFile != null ? appFile.getAbsolutePath() : ""), e);
        }
    }

    private File getApplicationDirectory(String applicationName) {
        File appDirectory = new File(getLocalApplicationsDirectory(), applicationName);
        appDirectory.mkdir();
        return appDirectory;
    }

    public File getRoot() {
        return getLocalRepository();
    }

    @SuppressWarnings("unchecked")
    public Collection<File> listFiles() {
        return FileUtils.listFiles(getLocalRepository(), null, true);
    }

    public void purge() throws IOException {
        logger.info(format("Purging local repository in %s", getLocalRepository()));
        FileUtils.deleteDirectory(getLocalRepository());
        bootstrap();
    }

    protected Logger getLogger() {
        return logger;
    }
}
