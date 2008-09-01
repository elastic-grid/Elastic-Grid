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

import com.elasticgrid.amazon.sdb.Item;
import com.elasticgrid.amazon.sdb.SimpleDBException;
import com.xerox.amazonws.sdb.ItemAttribute;
import com.xerox.amazonws.sdb.SDBException;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemImpl implements Item {
    private final com.xerox.amazonws.sdb.Item item;
    private static final Logger logger = Logger.getLogger(Item.class.getName());

    public ItemImpl(com.xerox.amazonws.sdb.Item item) {
        this.item = item;
    }

    public String getIdentifier() {
        return item.getIdentifier();
    }

    public List<ItemAttribute> getAttributes() throws SimpleDBException {
        try {
            List<com.xerox.amazonws.sdb.ItemAttribute> attrs = item.getAttributes();
            List<ItemAttribute> attributes = new ArrayList<ItemAttribute>(attrs.size());
            for (com.xerox.amazonws.sdb.ItemAttribute attribute : attrs) {
                attributes.add(new ItemAttribute(attribute));
            }
            return attributes;
        } catch (SDBException e) {
            throw new SimpleDBException("Can't get attributes of item " + getIdentifier(), e);
        }
    }

    public List<ItemAttribute> getAttributes(String attributeName) throws SimpleDBException {
        try {
            List<com.xerox.amazonws.sdb.ItemAttribute> attrs = item.getAttributes(attributeName);
            List<ItemAttribute> attributes = new ArrayList<ItemAttribute>(attrs.size());
            for (com.xerox.amazonws.sdb.ItemAttribute attribute : attrs) {
                attributes.add(new ItemAttribute(attribute));
            }
            return attributes;
        } catch (SDBException e) {
            throw new SimpleDBException("Can't get attributes of item " + getIdentifier(), e);
        }
    }

    public void putAttribute(String attrName, String attrValue, boolean replace) throws SimpleDBException {
        putAttributes(new ItemAttribute(attrName, attrValue, replace));
    }

    public void putAttributes(ItemAttribute... attributes) throws SimpleDBException {
        List<com.xerox.amazonws.sdb.ItemAttribute> attrs = new ArrayList<com.xerox.amazonws.sdb.ItemAttribute>(attributes.length);
        for (ItemAttribute attr : attributes) {
            logger.log(Level.INFO, "Adding to item {0} attribute {1}�with value {2}",
                    new Object[] { getIdentifier(), attr.getName(), attr.getValue() });
            attrs.add(new com.xerox.amazonws.sdb.ItemAttribute(attr.getName(), attr.getValue(), attr.isReplace()));
        }
        try {
            item.putAttributes(attrs);
        } catch (SDBException e) {
            throw new SimpleDBException("Can't put attributes on item " + getIdentifier(), e);
        }
    }

    public void deleteAttributes(ItemAttribute... attributes) throws SimpleDBException {
        List<com.xerox.amazonws.sdb.ItemAttribute> attrs = new ArrayList<com.xerox.amazonws.sdb.ItemAttribute>(attributes.length);
        for (ItemAttribute attr : attributes) {
            logger.log(Level.INFO, "Deleting from item {0} attribute {1}�with value {2}",
                    new Object[] { getIdentifier(), attr.getName(), attr.getValue() });
            attrs.add(new com.xerox.amazonws.sdb.ItemAttribute(attr.getName(), attr.getValue(), attr.isReplace()));
        }
        try {
            item.deleteAttributes(attrs);
        } catch (SDBException e) {
            throw new SimpleDBException("Can't delete attributes from item " + getIdentifier(), e);
        }
    }

}