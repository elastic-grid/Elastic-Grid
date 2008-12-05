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

import org.apache.commons.io.FileUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import java.io.File;
import java.io.IOException;
import static org.testng.Assert.*;
import static java.lang.String.format;

@ContextConfiguration(locations = {
        "/com/elasticgrid/repository/applicationContext.xml",
        "/com/elasticgrid/repository/applicationContext-ec2.xml"
})
public class RepositoryManagerTest extends AbstractTestNGSpringContextTests {
    private RepositoryManager repositoryManager;
    private LocalRepository localRepository;
    private RemoteRepository remoteRepository;

    @Test
    @Parameters(value = "numberOfFiles")
    public void testRestore(int numberOfFiles) throws IOException, InterruptedException {
        int before = localRepository.listFiles().size();
        for (int i = 0 ; i < numberOfFiles; i++) {
            File temp = new File(localRepository.getRoot(), format("elasticgrid-temp-%d.txt", i));
            FileUtils.writeStringToFile(temp, format("test %d, test %d, test %d", i, i, i));
            remoteRepository.upload(temp);
            temp.deleteOnExit();
        }
        repositoryManager.restore();
        assertEquals(localRepository.listFiles().size(), before + numberOfFiles);
        remoteRepository.purge();
        localRepository.purge();
        for (File f : localRepository.listFiles())
            System.out.printf("Found file %s\n", f.getAbsolutePath());
        assertEquals(localRepository.listFiles().size(), 0);
        repositoryManager.restore();
        assertEquals(localRepository.listFiles().size(), 0);
    }

    @BeforeClass
    public void setupServices() throws IOException {
        localRepository = (LocalRepository) applicationContext.getBean("localRepository", LocalRepository.class);
        remoteRepository = (RemoteRepository) applicationContext.getBean("remoteRepository", RemoteRepository.class);
        repositoryManager = (RepositoryManager) applicationContext.getBean("repositoryManager", RepositoryManager.class);
        assert localRepository != null;
        assert remoteRepository != null;
        assert repositoryManager != null;
        localRepository.bootstrap();
        localRepository.purge();
        assertEquals(localRepository.listFiles().size(), 0);
        remoteRepository.purge();
        repositoryManager.bootstrap();
    }

}
