/*
 * Copyright (c) 2007, Kalixia, SARL. All Rights Reserved.
 */
package org.jini.rio.tools.maven.rio;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import java.io.File;
import java.util.Map;

/**
 * Stops a Rio container.
 * @goal stop
 * @description Stops a Rio container.
 * @requiresDependencyResolution
 */
public class StopMojo extends AbstractRioMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Stopping Rio from " + getRioHome());
        try {
            String osName = System.getProperty("os.name");
            StringBuffer cmd = new StringBuffer(getRioHome());
            cmd.append(File.separatorChar).append("bin");
            cmd.append(File.separatorChar).append("rio");
            if (osName.startsWith("Windows")) {
                cmd.append(".cmd");
            }
            cmd.append(" destroy all");
            Process process = Runtime.getRuntime().exec(cmd.toString(), null, new File(getRioHome()));
            process.waitFor();
        } catch (Exception e) {
            throw new MojoExecutionException("Can't stop Rio", e);
        }
    }
}