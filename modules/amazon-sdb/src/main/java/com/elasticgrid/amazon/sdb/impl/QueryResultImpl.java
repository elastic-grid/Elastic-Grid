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

import com.elasticgrid.amazon.sdb.QueryResult;
import com.elasticgrid.amazon.sdb.Item;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

public class QueryResultImpl extends AbstractSimpleDBResult implements QueryResult {
    private final com.xerox.amazonws.sdb.QueryResult results;

    public QueryResultImpl(com.xerox.amazonws.sdb.QueryResult results) {
        super(results);
        this.results = results;
    }

    public List<Item> getItems() {
        List<com.xerox.amazonws.sdb.Item> raw = results.getItemList();
        List<Item> items = new ArrayList<Item>(raw.size());
        for (com.xerox.amazonws.sdb.Item i : raw) {
            items.add(new ItemImpl(i));
        }
        return items;
    }
}
