/*
 * Copyright (c) 2007, Kalixia, SARL. All Rights Reserved.
 */
package org.jini.rio.tools.maven.rio;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Build & deploy a Rio OAR.
 * @goal oar-complex
 * @description Build & deploy a Rio OAR
 * @phase package
 * @requiresProject true
 * @requiresDependencyResolution
 */
public class OarComplexMojo extends AbstractRioMojo {

    /**
     * OpString to deploy.
     * @parameter
     * @required
     */
    private String opstring;

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The dependencies of the Maven project.
     * @parameter expression="${project.artifacts}"
     */
    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
    private Collection<Artifact> dependencies;

    public void execute() throws MojoExecutionException, MojoFailureException {
        List<Artifact> jsbs = new ArrayList<Artifact>();

        getLog().info("Gathering list of JSBs...");
        for (Artifact artifact : dependencies) {
            if ("jsb".equals(artifact.getType())) {
                jsbs.add(artifact);
                getLog().info(
                        " -> " + artifact.getGroupId()
                                + ':' + artifact.getArtifactId()
                                + ':' + artifact.getVersion()
                );
            } else {
                getLog().debug("Skipping " + artifact);
            }
        }

        File deployDirectory = new File(getRioHome() + "/deploy");
        File opstringDeployDirectory = new File(deployDirectory, project.getArtifactId());
        opstringDeployDirectory.mkdir();
        getLog().info("Created OpString deployment directory in " + opstringDeployDirectory);
        new File(opstringDeployDirectory, "lib").mkdir();
        new File(opstringDeployDirectory, "lib-dl").mkdir();
        new File(opstringDeployDirectory, "lib-ui").mkdir();
        getLog().info("Copying JSB components...");
        for (Artifact artifact : jsbs) {
            String jarImplName = artifact.getFile().getAbsolutePath().replace(".jar", "-impl.jar");
            String jarClientName = jarImplName.replace("-impl.jar", "-dl.jar");
            String jarUiName = jarImplName.replace("-impl.jar", "-ui.jar");
            if (new File(jarImplName).exists()) {
                try {
                    FileUtils.copyFileToDirectory(new File(opstring), opstringDeployDirectory);
                    if (new File(jarImplName).exists())
                        FileUtils.copyFileToDirectory(new File(jarImplName), new File(opstringDeployDirectory, "lib"));
                    if (new File(jarClientName).exists())
                        FileUtils.copyFileToDirectory(new File(jarClientName), new File(opstringDeployDirectory, "lib-dl"));
                    if (new File(jarUiName).exists())
                        FileUtils.copyFileToDirectory(new File(jarUiName), new File(opstringDeployDirectory, "lib-ui"));
                } catch (IOException e) {
                    throw new MojoFailureException("Can't copy JSB files: " + e.getMessage());
                }
            }
        }

        getLog().info("Copying required dependencies...");
        for (Artifact artifact : dependencies) {
            if (!"compile".equals(artifact.getScope())) {
                getLog().info("Skipping " + artifact.getFile() + " (" + artifact.getScope() + ")");
                continue;
            }
            getLog().info(" -> " + artifact.toString());
            try {
                FileUtils.copyFileToDirectory(artifact.getFile(), new File(opstringDeployDirectory, "lib"));
            } catch (IOException e) {
                throw new MojoFailureException("Can't copy artifact " + artifact + ": " + e.getMessage());
            }
        }

//        deployOpstring(opstring);

        project.getArtifact().setFile(new File(opstring));
    }
}