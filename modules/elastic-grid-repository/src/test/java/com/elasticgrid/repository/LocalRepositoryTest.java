/**
 * Elastic Grid
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
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
import com.elasticgrid.repository.impl.LocalRepositoryImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.rmi.RemoteException;
import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * @author Jerome Bernard
 */
@ContextConfiguration(locations = {
        "/com/elasticgrid/repository/applicationContext.xml",
        "/com/elasticgrid/repository/applicationContext-ec2.xml"
})
public class LocalRepositoryTest extends AbstractTestNGSpringContextTests {
    private LocalRepository repository;
    private RepositoryManager manager;

    @Test
    public void testLoad() throws IOException {
        repository.purge();
        repository.load();
        assertEquals(repository.getGrids().size(), 0);
        assertEquals(repository.getApplications().size(), 0);

        // create an application and ensure its there now
        Application app = manager.application("test-app");
        assert app != null;
        app.createNewOAR().name("test-oar").version("1.0").opstring("test-opstring").deployDir("").activationType("");
        manager.save(app);
        assertEquals(repository.getGrids().size(), 0);
        assertEquals(repository.getApplications().size(), 1);
        assertEquals(repository.getApplications().entrySet().iterator().next().getValue().getName(), "test-app");

        Grid grid = manager.grid("test-grid");
        assert grid != null;       

        repository = new LocalRepositoryImpl();        
        repository.load();
        assertEquals(repository.getGrids().size(), 0);
        assertEquals(repository.getApplications().size(), 1);
        assertEquals(repository.getApplications().entrySet().iterator().next().getValue().getName(), "test-app");
    }

    @BeforeClass
    public void setupLocalRepository() throws RemoteException {
        repository = (LocalRepository) applicationContext.getBean("localRepository", LocalRepository.class);
        assert repository != null;
        repository.bootstrap();

        manager = (RepositoryManager) applicationContext.getBean("repositoryManager", RepositoryManager.class);
        assert manager != null;
    }

}
