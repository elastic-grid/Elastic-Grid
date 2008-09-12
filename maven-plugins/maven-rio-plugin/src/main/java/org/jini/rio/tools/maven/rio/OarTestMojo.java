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
import static java.lang.String.format;

/**
 * Build & deploy a Rio OAR test.
 * @goal oar-test
 * @phase test
 * @description Build & deploy a Rio OAR test
 * @requiresProject true
 * @requiresDependencyResolution
 */
public class OarTestMojo extends AbstractRioMojo {

    /**
     * OpString to deploy.
     * @parameter alias="opstring-test"
     */
    private String opstringTest;

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * Should tests be skipped?
     * @parameter expression="${maven.test.skip}
     */
    private boolean skipTest;

    /**
     * The dependencies of the Maven project.
     * @parameter expression="${project.artifacts}"
     */
    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
    private Collection<Artifact> dependencies;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipTest || opstringTest == null || "".equals(opstringTest))
            return;
        
        List<Artifact> jsbs = new ArrayList<Artifact>();

        getLog().info("Gathering list of JSBs for tests...");
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
        new File(opstringDeployDirectory, "lib-test").mkdir();
        getLog().info("Copying JSB components...");
        for (Artifact artifact : jsbs) {
            String jarImplName = artifact.getFile().getAbsolutePath().replace(".jar", "-impl.jar");
            String jarClientName = jarImplName.replace("-impl.jar", "-dl.jar");
            String jarUiName = jarImplName.replace("-impl.jar", "-ui.jar");
            String jarTestName = jarImplName.replace("-impl.jar", "-tests.jar");
            if (new File(jarImplName).exists()) {
                try {
                    FileUtils.copyFileToDirectory(new File(opstringTest), opstringDeployDirectory);
                    if (new File(jarImplName).exists())
                        FileUtils.copyFileToDirectory(new File(jarImplName), new File(opstringDeployDirectory, "lib"));
                    if (new File(jarClientName).exists())
                        FileUtils.copyFileToDirectory(new File(jarClientName), new File(opstringDeployDirectory, "lib-dl"));
                    if (new File(jarUiName).exists())
                        FileUtils.copyFileToDirectory(new File(jarUiName), new File(opstringDeployDirectory, "lib-ui"));
                    if (new File(jarTestName).exists())
                        FileUtils.copyFileToDirectory(new File(jarTestName), new File(opstringDeployDirectory, "lib-test"));
                    else
                        getLog().warn(format("Could not find test JSB jar %s", jarTestName));
                } catch (IOException e) {
                    throw new MojoFailureException("Can't copy JSB files: " + e.getMessage());
                }
            }
        }

        getLog().info("Copying required dependencies...");
        for (Artifact artifact : dependencies) {
            if (!"compile".equals(artifact.getScope()) && !"test".equals(artifact.getScope())) {
                getLog().info("Skipping " + artifact.getFile() + " (" + artifact.getScope() + ")");
                continue;
            }
            getLog().info(" -> " + artifact.toString());
            try {
                if ("compile".equals(artifact.getScope()))
                    FileUtils.copyFileToDirectory(artifact.getFile(), new File(opstringDeployDirectory, "lib"));
                else
                    FileUtils.copyFileToDirectory(artifact.getFile(), new File(opstringDeployDirectory, "lib-test"));
            } catch (IOException e) {
                throw new MojoFailureException("Can't copy artifact " + artifact + ": " + e.getMessage());
            }
        }

        deployOpstring(opstringTest);
    }
}