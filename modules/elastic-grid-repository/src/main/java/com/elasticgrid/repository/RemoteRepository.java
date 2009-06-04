/**
 * Elastic Grid
 * Copyright (C) 2008-2009 Elastic Grid, LLC.
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
package com.elasticgrid.repository;

import com.elasticgrid.model.Application;

import java.rmi.RemoteException;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Remote repository.
 * @author Jerome Bernard
 */
public interface RemoteRepository extends Repository {

	/**
     * Restore the local file-system from the content of the remote repository.
     * @throws RemoteException if there is a network failure
     */
    void restore() throws RemoteException;

    /**
     * Upload a local file to the remote repository.
     * @param file the local file to upload
     * @return the URL of the uploaded file
     * @throws FileNotFoundException if the local file can't be found
     * @throws RemoteException if there is a network failure
     */
    String upload(File file) throws RemoteException, FileNotFoundException;

}
