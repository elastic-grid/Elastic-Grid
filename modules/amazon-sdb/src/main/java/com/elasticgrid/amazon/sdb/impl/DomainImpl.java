/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This file is part of Elastic Grid.
 * 
 * Elastic Grid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * 
 * Elastic Grid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Elastic Grid.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elasticgrid.amazon.sdb.impl;

import com.elasticgrid.amazon.sdb.Domain;
import com.elasticgrid.amazon.sdb.Item;
import com.elasticgrid.amazon.sdb.SimpleDBException;
import com.elasticgrid.amazon.sdb.QueryResult;
import com.xerox.amazonws.sdb.SDBException;

import java.util.logging.Logger;
import java.util.logging.Level;

public class DomainImpl implements Domain {
    private final com.xerox.amazonws.sdb.Domain domain;
    private static final Logger logger = Logger.getLogger(Domain.class.getName());

    public DomainImpl(com.xerox.amazonws.sdb.Domain domain) {
        this.domain = domain;
    }

    public String getName() {
        return domain.getName();
    }

    public Item findItem(String identifier) throws SimpleDBException {
        try {
            logger.log(Level.INFO, "Searching for item {0}", identifier);
            return new ItemImpl(domain.getItem(identifier));
        } catch (SDBException e) {
            throw new SimpleDBException("Can't find item " + identifier, e);
        }
    }

    public void deleteItem(String identifier) throws SimpleDBException {
        try {
            logger.log(Level.INFO, "Deleting item {0}", identifier);
            domain.deleteItem(identifier);
        } catch (SDBException e) {
            throw new SimpleDBException("Can't delete item " + identifier, e);
        }
    }

    public QueryResult listItems() throws SimpleDBException {
        try {
            logger.log(Level.INFO, "Searching for items in domain {0}", getName());
            return new QueryResultImpl(domain.listItems());
        } catch (SDBException e) {
            throw new SimpleDBException("Can't list items in domain " + getName(), e);
        }
    }

    public QueryResult listItems(String query) throws SimpleDBException {
        try {
            logger.log(Level.INFO, "Searching for items in domain {0} with query {1}", new Object[] { getName(), query });
            return new QueryResultImpl(domain.listItems(query));
        } catch (SDBException e) {
            throw new SimpleDBException("Can't list items in domain " + getName() + " for query " + query, e);
        }
    }

    public QueryResult listItems(String query, String nextToken) throws SimpleDBException {
        try {
            logger.log(Level.INFO, "Searching for items in domain {0} with query {1}", new Object[] { getName(), query });
            return new QueryResultImpl(domain.listItems(query, nextToken));
        } catch (SDBException e) {
            throw new SimpleDBException("Can't list items in domain " + getName() + " for query " + query, e);
        }
    }

    public QueryResult listItems(String query, String nextToken, int maxResults) throws SimpleDBException {
        try {
            logger.log(Level.INFO, "Searching for items in domain {0} with query {1}", new Object[] { getName(), query });
            return new QueryResultImpl(domain.listItems(query, nextToken, maxResults));
        } catch (SDBException e) {
            throw new SimpleDBException("Can't list items in domain " + getName() + " for query " + query, e);
        }
    }

}
