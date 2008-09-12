/*
 * Copyright (c) 2007, Kalixia, SARL. All Rights Reserved.
 */
package org.jini.rio.tools.maven.rio;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import java.io.File;
import java.util.Map;

/**
 * Undeploys a Rio OpString from a running Rio container.
 * @goal undeploy
 * @description Undeploys a Rio OpString from a running Rio container
 * @requiresProject true
 * @requiresDependencyResolution
 */
public class UndeployOpStringMojo extends AbstractRioMojo {

    /**
     * OpString to undeploy.
     * @parameter
     * @required
     */
    private String opstring;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Undeploying OpString " + opstring + "...");
        try {
            String osName = System.getProperty("os.name");
            StringBuffer cmd = new StringBuffer(getRioHome());
            cmd.append(File.separatorChar).append("bin");
            cmd.append(File.separatorChar).append("rio");
            if (osName.startsWith("Windows")) {
                cmd.append(".cmd");
            }
            cmd.append(" undeploy ");
            cmd.append(opstring);
            Process process = Runtime.getRuntime().exec(cmd.toString(), null, new File(getRioHome()));
            process.waitFor();
        } catch (Exception e) {
            throw new MojoExecutionException("Can't undeploy OpString in Rio", e);
        }
    }
}