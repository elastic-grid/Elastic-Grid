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

package com.elasticgrid.boot;

import com.sun.jini.start.ServiceDescriptor;
import java.io.IOException;
import java.io.File;
import org.rioproject.boot.BootUtil;
import org.rioproject.boot.RioServiceDescriptor;

public class ServiceDescriptorUtil extends org.rioproject.boot.ServiceDescriptorUtil {

    /**
     * Get the {@link com.sun.jini.start.ServiceDescriptor} instance for
     * {@link org.rioproject.monitor.ProvisionMonitor} using the Webster port
     * created by this utility.
     *
     * @param policy The security policy file to use
     * @param monitorConfig The configuration file the Monitor will use
     * @return The {@link com.sun.jini.start.ServiceDescriptor} instance for
     * the Monitor using an anonymous port.
     *
     * @throws IOException If there are problems getting the anonymous port
     * @throws RuntimeException If the <tt>EG_HOME</tt> system property is not
     * set
     */
    public static ServiceDescriptor getMonitor(String policy, String monitorConfig) throws IOException {
        return getMonitor(policy, monitorConfig, null);
    }

    /**
     * Get the {@link com.sun.jini.start.ServiceDescriptor} instance for
     * {@link org.rioproject.monitor.ProvisionMonitor} using the Webster port
     * created by this utility.
     *
     * @param policy The security policy file to use
     * @param monitorConfig The configuration file the Monitor will use
     * @param overrides The configuration overrides to use
     * @return The {@link com.sun.jini.start.ServiceDescriptor} instance for
     * the Monitor using an anonymous port.
     *
     * @throws IOException If there are problems getting the anonymous port
     * @throws RuntimeException If the <tt>EG_HOME</tt> system property is not
     * set
     */
    public static ServiceDescriptor getMonitor(String policy, String monitorConfig, String[] overrides) throws IOException {
        return getMonitor(policy, monitorConfig, overrides, getAnonymousPort());
    }

    /**
     * Get the {@link com.sun.jini.start.ServiceDescriptor} instance for
     * {@link org.rioproject.monitor.ProvisionMonitor}.
     *
     * @param policy The security policy file to use
     * @param monitorConfig The configuration file the Monitor will use
     * @param overrides The configuration overrides to use
     * @param port The port to use when constructing the codebase
     * @return The {@link com.sun.jini.start.ServiceDescriptor} instance for
     * the Monitor using an anonymous port.
     *
     * @throws IOException If there are problems getting the anonymous port
     * @throws RuntimeException If the <tt>EG_HOME</tt> system property is not
     * set
     */
    public static ServiceDescriptor getMonitor(String policy, String monitorConfig, String[] overrides, String port) throws IOException {
        return getMonitor(policy, monitorConfig, overrides, BootUtil.getHostAddress(), port);
    }

    /**
     * Get the {@link com.sun.jini.start.ServiceDescriptor} instance for
     * {@link org.rioproject.monitor.ProvisionMonitor}.
     *
     * @param policy The security policy file to use
     * @param monitorConfig The configuration file the Monitor will use
     * @param overrides The configuration overrides to use
     * @param hostAddress The address to use when constructing the codebase
     * @param port The port to use when constructing the codebase
     * @return The {@link com.sun.jini.start.ServiceDescriptor} instance for
     * the Monitor using an anonymous port.
     *
     * @throws IOException If there are problems getting the anonymous port
     * @throws RuntimeException If the <tt>EG_HOME</tt> system property is not
     * set
     */
    public static ServiceDescriptor getMonitor(String policy, String monitorConfig, String[] overrides, String hostAddress, String port) throws IOException {
        String egHome = System.getProperty("EG_HOME");
        if (egHome == null)
            throw new RuntimeException("EG_HOME property not declared");

        String[] configArgs = getArray(monitorConfig, overrides);
        String monitorClasspath =
            egHome + File.separator + "lib" + File.separator + "monitor.jar"
            + File.pathSeparator +
            // TODO: get rid of this version number!
            egHome + File.separator + "lib" + File.separator + "elastic-grid" + File.separator + "kernel" + File.separator + "amazon-ec2-provisioner-0.8.3.jar"
            ;
        String monitorCodebase = BootUtil.getCodebase(new String[] { "monitor-dl.jar", "rio-dl.jar", "jsk-dl.jar"}, hostAddress, port);
        String implClass = "org.rioproject.monitor.ProvisionMonitorImpl";
        return new RioServiceDescriptor(monitorCodebase, policy, monitorClasspath, implClass, configArgs);

    }


    public static ServiceDescriptor getRestApi(String policy, String restApiConfig) throws IOException {
        return getRestApi(policy, restApiConfig, null);
    }

    public static ServiceDescriptor getRestApi(String policy, String restApiConfig, String[] overrides) throws IOException {
        return getRestApi(policy, restApiConfig, overrides, getAnonymousPort());
    }

    public static ServiceDescriptor getRestApi(String policy, String restApiConfig, String[] overrides, String port) throws IOException {
        return getRestApi(policy, restApiConfig, overrides, BootUtil.getHostAddress(), port);
    }

    public static ServiceDescriptor getRestApi(String policy, String restApiConfig, String[] overrides, String hostAddress, String port) throws IOException {
        String egHome = System.getProperty("EG_HOME");
        if (egHome == null)
            throw new RuntimeException("EG_HOME property not declared");
        String[] configArgs = getArray(restApiConfig, overrides);
        String restApiRoot = egHome + File.separator + "lib" + File.separator + "elastic-grid" + File.separator + "applications" + File.separator + "rest-api";
        String restApiClasspath = restApiRoot + File.separator + "rest-api-0.8.3.jar";      // TODO: get rid of this version number!
        String restApiCodebase = BootUtil.getCodebase(new String[] { "rio-dl.jar", "jsk-dl.jar"}, hostAddress, port);
        String implClass = "com.elasticgrid.rest.RestJSB";
        return new RioServiceDescriptor(restApiCodebase, policy, restApiClasspath, implClass, configArgs);
    }

}