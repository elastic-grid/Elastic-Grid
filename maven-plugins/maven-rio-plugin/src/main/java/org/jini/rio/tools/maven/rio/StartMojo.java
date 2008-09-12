/*
 * Copyright (c) 2007, Kalixia, SARL. All Rights Reserved.
 */
package org.jini.rio.tools.maven.rio;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import java.io.File;
import java.io.IOException;

/**
 * Starts a Rio container.
 * @goal start
 * @description Starts a Rio container.
 * @requiresDependencyResolution
 */
public class StartMojo extends AbstractRioMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Starting Rio from " + getRioHome());
        try {
            String osName = System.getProperty("os.name");
            StringBuffer cmd = new StringBuffer();
            cmd.append(File.separatorChar).append("bin");
            cmd.append(File.separatorChar).append("rio");
            if (osName.startsWith("Windows")) {
                cmd.append(".cmd");
            }
            cmd.append(" start all");
            getLog().info("Running " + cmd.toString());
            Runtime.getRuntime().exec(cmd.toString(), null, new File(getRioHome()));
        } catch (IOException e) {
            throw new MojoExecutionException("Can't start Rio", e);
        }
    }
}
