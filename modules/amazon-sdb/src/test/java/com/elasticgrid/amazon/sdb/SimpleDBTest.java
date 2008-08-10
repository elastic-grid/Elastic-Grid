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

package com.elasticgrid.amazon.sdb;

import com.elasticgrid.amazon.sdb.impl.SimpleDBImpl;
import com.elasticgrid.utils.amazon.AWSUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Properties;
import java.util.List;

public class SimpleDBTest {
    private SimpleDB sdb;

    @Test
    public void testDomainCreation() throws SimpleDBException {
        Domain domain = sdb.createDomain("test");
        assert domain != null;
        List<Domain> domains = sdb.listDomains();
        assert domains != null;
        assert domains.size() > 0;
        domain = sdb.findDomain("test");
        assert domain != null;
        assert "test".equals(domain.getName());
        assert domain.listItems() != null;
        assert domain.listItems().getItems() != null;
        assert domain.listItems().getItems().size() == 0;
        sdb.deleteDomain(domain.getName());
    }

    @Test
    public void testItemCreation() throws SimpleDBException {
        Domain domain = sdb.createDomain("test");
        assert domain != null;
        domain.createItem("test_item", "a value");
        sdb.deleteDomain(domain.getName());
    }

    @BeforeClass
    public void setupSimpleDB() throws Exception {
        sdb = new SimpleDBImpl();
        Properties awsProps = AWSUtils.loadEC2Configuration();
        ((SimpleDBImpl) sdb).setAwsAccessID((String) awsProps.get(AWSUtils.AWS_ACCESS_ID));
        ((SimpleDBImpl) sdb).setAwsSecretKey((String) awsProps.get(AWSUtils.AWS_SECRET_KEY));
        ((SimpleDBImpl) sdb).afterPropertiesSet();
    }

}
