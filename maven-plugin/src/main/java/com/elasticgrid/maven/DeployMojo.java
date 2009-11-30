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
package com.elasticgrid.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Deploys an Elastic Grid OpString to the repository.
 *
 * @goal deploy
 * @description Deploys an Elastic Grid OpString to the repository
 * @requiresProject true
 * @requiresDependencyResolution
 */
public class DeployMojo extends AbstractMojo {
    /**
     * OpString to deploy.
     * @parameter
     * @required
     */
    private String opstring;

    public void execute() throws MojoExecutionException, MojoFailureException {
        System.out.println("Should install OAR!!");
        //TODO: do something!
    }
}
