/**
 * Elastic Grid
 * Copyright (C) 2008-2010 Elastic Grid, LLC.
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
package com.elasticgrid.amazon.sdb.impl;

import com.elasticgrid.amazon.sdb.SimpleDB;
import com.elasticgrid.amazon.sdb.Domain;
import com.elasticgrid.amazon.sdb.SimpleDBException;
import com.xerox.amazonws.sdb.SDBException;
import com.xerox.amazonws.sdb.ListDomainsResult;
import org.springframework.beans.factory.InitializingBean;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.List;
import java.util.ArrayList;

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

    public void setAwsAccessID(String awsAccessID) {
        this.awsAccessID = awsAccessID;
    }

    public void setAwsSecretKey(String awsSecretKey) {
        this.awsSecretKey = awsSecretKey;
    }

    public void afterPropertiesSet() throws Exception {
        sdb = new com.xerox.amazonws.sdb.SimpleDB(awsAccessID, awsSecretKey);
    }
}
