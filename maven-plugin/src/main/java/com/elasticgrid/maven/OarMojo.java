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
package com.elasticgrid.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.rioproject.tools.maven.CreateJar;
import java.util.Collection;
import java.util.List;

/**
 * Properly packages an OAR (Operational String Archive).
 *
 * @extendsPlugin rio
 * @goal oar
 * @description Build & deploy an Elastic Grid OAR
 * @phase package
 * @requiresProject true
 * @requiresDependencyResolution
 */
public class OarMojo extends org.rioproject.tools.maven.OarMojo {
    /**
     * OpString to deploy.
     *
     * @parameter
     * @required
     */
    private String opstring;

    /**
     * Name of the OAR.
     *
     * @parameter
     */
    private String oarName;

    /**
     * The OAR to generate.
     *
     * @parameter expression="${project.build.directory}/${project.build.finalName}.oar"
     * @required
     */
    private String oarFileName;

    /**
     * List of jars to create
     *
     * @parameter
     */
    private List<CreateJar> createJars;

    /**
     * Create a single jar
     *
     * @parameter
     */
    private CreateJar createJar;

    /**
     * Dependency artifacts
     *
     * @parameter expression="${project.dependencyArtifacts}"
     */
    private Collection dependencies;

    /**
	 * The Jar archiver.
	 *
	 * @parameter expression="${component.org.codehaus.plexus.archiver.Archiver#jar}"
	 * @required
	 */
	private JarArchiver jarArchiver;

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @component
     */
    private MavenProjectHelper projectHelper;

    @Override
    protected String getOpstring() {
        return opstring;
    }

    @Override
    protected String getOarName() {
        return oarName;
    }

    @Override
    protected String getOarFileName() {
        return oarFileName;
    }

    @Override
    protected MavenProject getMavenProject() {
        return project;
    }

    @Override
    protected List<CreateJar> getCreateJars() {
        return createJars;
    }

    @Override
    protected CreateJar getCreateJar() {
        return createJar;
    }

    @Override
    protected JarArchiver getJarArchiver() {
        return jarArchiver;
    }

    @Override
    protected MavenProjectHelper getProjectHelper() {
        return projectHelper;
    }

    @Override
    protected Collection getDependencies() {
        return dependencies;
    }
}
