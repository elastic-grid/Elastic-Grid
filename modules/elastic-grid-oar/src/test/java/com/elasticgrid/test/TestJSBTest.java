/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * Licensed under the GNU Lesser General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
