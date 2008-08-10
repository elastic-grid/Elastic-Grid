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

package com.elasticgrid.amazon.sdb.impl;

import com.elasticgrid.amazon.sdb.SimpleDB;
import com.elasticgrid.amazon.sdb.Domain;
import com.elasticgrid.amazon.sdb.SimpleDBException;
import com.xerox.amazonws.sdb.SDBException;
import com.xerox.amazonws.sdb.ListDomainsResult;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.List;
import java.util.ArrayList;

@Service
public class SimpleDBImpl implements SimpleDB, InitializingBean {
    private com.xerox.amazonws.sdb.SimpleDB sdb;
    private String awsAccessID, awsSecretKey;
    private static final Logger logger = Logger.getLogger(SimpleDB.class.getName());

    public Domain createDomain(String name) throws SimpleDBException {
        try {
            logger.log(Level.INFO, "Creating domain {0}", name);
            return new DomainImpl(sdb.createDomain(name));
        } catch (SDBException e) {
            throw new SimpleDBException("Can't create domain " + name, e);
        }
    }

    public void deleteDomain(String name) throws SimpleDBException {
        try {
            logger.log(Level.INFO, "Deleting domain {0}", name);
            sdb.deleteDomain(name);
        } catch (SDBException e) {
            throw new SimpleDBException("Can't delete domain " + name, e);
        }
    }

    public Domain findDomain(String name) throws SimpleDBException {
        try {
            logger.log(Level.INFO, "Searching for domain {0}", name);
            return new DomainImpl(sdb.getDomain(name));
        } catch (SDBException e) {
            throw new SimpleDBException("Can't find domain " + name, e);
        }
    }

    public List<Domain> listDomains() throws SimpleDBException {
        try {
            logger.info("Searching for all domains");
            ListDomainsResult raw = sdb.listDomains();
            List<Domain> domains = new ArrayList<Domain>(raw.getDomainList().size());
            for (com.xerox.amazonws.sdb.Domain domain : raw.getDomainList()) {
                domains.add(new DomainImpl(domain));
            }
            logger.log(Level.INFO, "Found {0} domain(s)", domains.size());
            return domains;
        } catch (SDBException e) {
            throw new SimpleDBException("Can't list domains", e);
        }
    }

    @Required
    public void setAwsAccessID(String awsAccessID) {
        this.awsAccessID = awsAccessID;
    }

    @Required
    public void setAwsSecretKey(String awsSecretKey) {
        this.awsSecretKey = awsSecretKey;
    }

    public void afterPropertiesSet() throws Exception {
        sdb = new com.xerox.amazonws.sdb.SimpleDB(awsAccessID, awsSecretKey);
    }
}
