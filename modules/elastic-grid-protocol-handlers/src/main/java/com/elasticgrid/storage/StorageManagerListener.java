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
package com.elasticgrid.storage;

import org.rioproject.associations.Association;
import org.rioproject.associations.AssociationListener;

/**
 * An AssociationListener for Storage Manager instances
 */
class StorageManagerListener implements AssociationListener<StorageManager> {
    public void discovered(Association association, StorageManager storageManager) {
        Handler.setStorageManager(storageManager);
    }

    public void changed(Association association, StorageManager storageManager) {
    }

    public void broken(Association association, StorageManager storageManager) {
        Handler.setStorageManager(null);
    }
}