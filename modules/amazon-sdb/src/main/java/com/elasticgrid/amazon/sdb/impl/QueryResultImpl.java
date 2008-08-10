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
