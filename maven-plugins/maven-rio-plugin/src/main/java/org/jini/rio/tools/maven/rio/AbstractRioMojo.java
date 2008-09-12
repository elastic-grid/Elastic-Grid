/*
 * Copyright (c) 2007, Kalixia, SARL. All Rights Reserved.
 */
package org.jini.rio.tools.maven.rio;

import org.apache.tools.ant.Project;
import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import java.util.Iterator;
import java.util.Map;
import java.io.File;

public abstract class AbstractRioMojo extends AbstractMojo {

    /**
     * The environment configuration Map.
     *
     * @parameter
     */
    private Map environment;

    /**
     * The timeout for deployment/undeployment.
     *
     * @parameter
     */
    private long timeout = 30000l;

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    protected Project getProject() {
        // build Ant project
        Project antProject = new Project();
        for (Iterator iterator = environment.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            antProject.setProperty((String) entry.getKey(), (String) entry.getValue());
        }
        return antProject;
    }

    protected String getRioHome() {
        String home = (String) environment.get("rio.home");
        if (home == null)
            home = (String) environment.get("RIO_HOME");
        return home;
    }

    protected String getJiniHome() {
        String home = (String) environment.get("jini.home");
        if (home == null)
            home = (String) environment.get("JINI_HOME");
        return home;
    }

    protected void deployOpstring(String opstring) throws MojoExecutionException {
        undeployOpstring(opstring);
        getLog().info("Deploying OpString " + opstring + "...");
        try {
            String osName = System.getProperty("os.name");
            StringBuffer cmd = new StringBuffer(getRioHome());
            cmd.append(File.separatorChar).append("bin");
            cmd.append(File.separatorChar).append("rio");
            if (osName.startsWith("Windows")) {
                cmd.append(".cmd");
            }
            cmd.append(' ');
            cmd.append("deploy -uv ");
            cmd.append("-t=").append(30000).append(' ');
            cmd.append(opstring);
            Process process = Runtime.getRuntime().exec(cmd.toString(), null, new File(getRioHome()));
            process.waitFor();
        } catch (Exception e) {
            throw new MojoExecutionException("Can't deploy OpString in Rio", e);
        }
    }

    protected void undeployOpstring(String opstring) throws MojoExecutionException  {
        getLog().info("Undeploying OpString " + opstring + "...");
        try {
            String osName = System.getProperty("os.name");
            StringBuffer cmd = new StringBuffer(getRioHome());
            cmd.append(File.separatorChar).append("bin");
            cmd.append(File.separatorChar).append("rio");
            if (osName.startsWith("Windows")) {
                cmd.append(".cmd");
            }
            cmd.append(' ');
            cmd.append("undeploy ");
            cmd.append(opstring);
            Process process = Runtime.getRuntime().exec(cmd.toString(), null, new File(getRioHome()));
            process.waitFor();
        } catch (Exception e) {
            throw new MojoExecutionException("Can't undeploy OpString in Rio", e);
        }
    }

}
