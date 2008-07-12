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

package com.elasticgrid.tools.cli;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

/**
 * CLI tests.
 */
@ContextConfiguration(locations = {
            "/applicationContext.xml",
            "/com/elasticgrid/grid/applicationContext.xml",
            "/com/elasticgrid/repository/applicationContext-ec2.xml"
        }
)
public class CLITests extends AbstractTestNGSpringContextTests {
    private CLI cli;

    @Test
    public void testCreateGrid() {
        cli.parse("grid create test --size=10");
    }

    @BeforeTest
    public void setupCLI() {
        cli = (CLI) applicationContext.getBean("cli");
        assert cli != null;
    }

}
