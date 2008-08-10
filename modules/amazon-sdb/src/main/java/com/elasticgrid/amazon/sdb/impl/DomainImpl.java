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

import com.elasticgrid.amazon.sdb.Domain;
import com.elasticgrid.amazon.sdb.Item;
import com.elasticgrid.amazon.sdb.SimpleDBException;
import com.elasticgrid.amazon.sdb.QueryResult;
import com.xerox.amazonws.sdb.SDBException;

public class DomainImpl implements Domain {
    private final com.xerox.amazonws.sdb.Domain domain;

    public DomainImpl(com.xerox.amazonws.sdb.Domain domain) {
        this.domain = domain;
    }

    public String getName() {
        return domain.getName();
    }

    public Item findItem(String identifier) throws SimpleDBException {
        try {
            return new ItemImpl(domain.getItem(identifier));
        } catch (SDBException e) {
            throw new SimpleDBException("Can't find item " + identifier, e);
        }
    }

    public void deleteItem(String identifier) throws SimpleDBException {
        try {
            domain.deleteItem(identifier);
        } catch (SDBException e) {
            throw new SimpleDBException("Can't delete item " + identifier, e);
        }
    }

    public QueryResult listItems() throws SimpleDBException {
        try {
            return new QueryResultImpl(domain.listItems());
        } catch (SDBException e) {
            throw new SimpleDBException("Can't list items in domain " + getName(), e);
        }
    }

    public QueryResult listItems(String query) throws SimpleDBException {
        try {
            return new QueryResultImpl(domain.listItems(query));
        } catch (SDBException e) {
            throw new SimpleDBException("Can't list items in domain " + getName() + " for query " + query, e);
        }
    }

    public QueryResult listItems(String query, String nextToken) throws SimpleDBException {
        try {
            return new QueryResultImpl(domain.listItems(query, nextToken));
        } catch (SDBException e) {
            throw new SimpleDBException("Can't list items in domain " + getName() + " for query " + query, e);
        }
    }

    public QueryResult listItems(String query, String nextToken, int maxResults) throws SimpleDBException {
        try {
            return new QueryResultImpl(domain.listItems(query, nextToken, maxResults));
        } catch (SDBException e) {
            throw new SimpleDBException("Can't list items in domain " + getName() + " for query " + query, e);
        }
    }

    public Item addItem(String name, String value) throws SimpleDBException {
        return new ItemImpl(new com.xerox.amazonws.sdb.Item());
    }
}
