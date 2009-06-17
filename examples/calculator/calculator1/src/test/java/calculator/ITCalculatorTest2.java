/*
 * Copyright to the original author or authors.
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

import org.rioproject.test.RioTestRunner;
import org.rioproject.test.SetTestManager;
import org.rioproject.test.TestManager;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.rmi.RemoteException;

/**
 * Example testing the Calculator service and it's required services from the
 * <tt>OperationalString</tt>
 */
@RunWith(RioTestRunner.class)
public class ITCalculatorTest2 {
    Calculator calculator;

    @SetTestManager
    static TestManager testManager;

    @Before
    public void setupCalculator() throws Exception {
        Assert.assertNotNull(testManager);
        testManager.startReggie();
//        testManager.startProvisionMonitor();
//        testManager.startCybernode();
//        testManager.deploy(new File(System.getProperty("opstring")));
//        ServiceTemplate template = new ServiceTemplate(null, new Class[]{Calculator.class}, null);
//        calculator = (Calculator) testManager.getClient().getRegistrars()[0].lookup(template);
    }

    @Test
    public void testCalculator() throws RemoteException {
        Assert.assertNotNull(calculator);
        add();
        subtract();
        divide();
        multiply();
    }

    void add() throws RemoteException {
        double val = calculator.add(3, 2);
        assertEquals(val, 5, 0);
        System.out.println("    3 + 2 = " + val);
    }

    void subtract() throws RemoteException {
        double val = calculator.subtract(3, 2);
        assertEquals(val, 1, 0);
        System.out.println("    3 - 2 = " + val);
    }

    void divide() throws RemoteException {
        double val = calculator.divide(10, 10);
        assertEquals(val, 1, 0);
        System.out.println("    10 % 10 = " + val);
    }

    void multiply() throws RemoteException {
        double val = calculator.multiply(10, 10);
        assertEquals(val, 100, 0);
        System.out.println("    10 * 10 = " + val);
    }

}