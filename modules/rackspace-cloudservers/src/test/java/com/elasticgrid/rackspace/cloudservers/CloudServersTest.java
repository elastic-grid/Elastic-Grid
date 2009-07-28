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
package com.elasticgrid.rackspace.cloudservers;

import com.elasticgrid.utils.rackspace.RackspaceUtils;
import com.elasticgrid.rackspace.common.RackspaceException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.io.IOException;
import java.util.List;

/**
 * Tests for the Rackspace Cloud Servers API.
 *
 * @author Jerome Bernard
 */
public class CloudServersTest {
    private CloudServers api;

    @Test
    public void testListServersWhenNoneRunning() throws CloudServersException {
        List<Server> servers = api.getServers();
        assert servers != null;
        assert servers.size() == 0;
    }

    @Test(expectedExceptions = CloudServersException.class)
    public void testRetreiveServerDetailsOfNonExistingServer() throws CloudServersException {
        api.getServerDetails(123);
    }

    @Test(expectedExceptions = CloudServersException.class)
    public void testUpdateServerNameAndPasswordOnNonExistingServer() throws CloudServersException {
        api.updateServerNameAndPassword(123, "new-name", "new-password");
    }

    @Test
    public void testLimits() throws CloudServersException {
        Limits limits = api.getLimits();
        assert limits != null;
        List<RateLimit> rateLimits = limits.getRateLimits();
        assert rateLimits.size() > 0;
        List<AbsoluteLimit> absoluteLimits = limits.getAbsoluteLimits();
        assert absoluteLimits.size() > 0;
    }

    @BeforeTest
    private void setupAPI() throws RackspaceException, IOException {
        String username = RackspaceUtils.getUsername();
        String apiKey = RackspaceUtils.getApiKey();
        api = new XMLCloudServers(username, apiKey);
    }

//    static {
//        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
//        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
//        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "debug");
//    }
}
