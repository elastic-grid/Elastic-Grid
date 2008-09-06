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

package com.elasticgrid.test;

import com.elasticgrid.TestInterface;
import org.testng.annotations.Test;
import java.rmi.RemoteException;

public class TestJSBTest extends AbstractJiniTest {

    @Test(groups = { "qa" })
    public void doLookupService() throws InterruptedException, RemoteException {
        TestInterface proxy = getService(TestInterface.class);
        for (int i = 0; i < 100; i++) {
            assert "ping".equals(proxy.echo("ping"));
            Thread.sleep(100 - i);
        }
    }

}
