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

package com.elasticgrid.amazon.sdb;

import com.xerox.amazonws.sdb.ItemAttribute;

import java.util.List;
import java.io.Serializable;

public interface Item {
    String getIdentifier();
    List<ItemAttribute> getAttributes() throws SimpleDBException;
    List<ItemAttribute> getAttributes(String attributeName) throws SimpleDBException;
    void putAttribute(String attrName, String attrValue, boolean replace) throws SimpleDBException;
    void putAttributes(ItemAttribute... attributes) throws SimpleDBException;
    void deleteAttributes(ItemAttribute... attributes) throws SimpleDBException;

    class ItemAttribute implements Serializable {
        private final String name;
        private final String value;
        private final boolean replace;

        public ItemAttribute(String name, String value, boolean replace) {
            this.name = name;
            this.value = value;
            this.replace = replace;
        }

        public ItemAttribute(com.xerox.amazonws.sdb.ItemAttribute attribute) {
            this.name = attribute.getName();
            this.value = attribute.getValue();
            this.replace = attribute.isReplace();
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public boolean isReplace() {
            return replace;
        }
    }
}
