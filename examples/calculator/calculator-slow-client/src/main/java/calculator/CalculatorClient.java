/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package calculator;

import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.ServiceDiscoveryManager;

import java.io.IOException;
import java.rmi.RMISecurityManager;
import java.security.Permission;

public class CalculatorClient {
    /*
      * This is for ease of use, for the invocation of the example. When
      * deploying outside of the example, this should be removed
      */
    static {
        System.setSecurityManager(new RMISecurityManager() {
            public void checkPermission(Permission perm) {
            }
        });
    }
    static Calculator calculator;

    public static void discover() throws IOException, InterruptedException {
        Class[] classes = new Class[]{Calculator.class};
        ServiceTemplate tmpl = new ServiceTemplate(null, classes, null);
        LookupDiscoveryManager ldm = new LookupDiscoveryManager(LookupDiscoveryManager.ALL_GROUPS, null, null);
        System.out.println("Discovering Calculator service ...");
        ServiceDiscoveryManager sdm = new ServiceDiscoveryManager(ldm, new LeaseRenewalManager());
        /* Wait no more then 10 seconds to discover the service */
        ServiceItem item = sdm.lookup(tmpl, null, 10000);
        if (item == null) {
            System.err.println("Could not locate calculator service!");
            System.exit(-1);
        }
        calculator = (Calculator) item.service;
    }

    public static void main(String args[]) throws IOException, InterruptedException {
        CalculatorClient.discover();
        calculator.add(3, 2);
        calculator.subtract(3, 2);
        calculator.multiply(3, 2);
        calculator.divide(3, 2);
    }
}
