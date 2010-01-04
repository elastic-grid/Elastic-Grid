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
package com.elasticgrid.admin;

import net.jini.config.Configuration;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.rioproject.core.jsb.ServiceBeanContext;
import org.rioproject.jsb.ServiceBeanAdapter;
import org.rioproject.jsb.ServiceBeanActivation;
import org.rioproject.associations.AssociationDescriptor;
import org.rioproject.associations.AssociationType;
import org.rioproject.associations.AssociationMgmt;
import org.rioproject.monitor.ProvisionMonitor;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import com.sun.jini.start.LifeCycle;
import com.elasticgrid.cluster.ClusterManager;
import com.elasticgrid.storage.StorageManager;

/** JSB for the administration console. Deploys an instance of Jetty on a configured port. */
public class AdminConsoleJSB extends ServiceBeanAdapter {
    private Server server;

    /** Component name we use to find items in the Configuration */
    static final String EG_CONFIG_COMPONENT = "com.elasticgrid";
    /** Component name we use to find items in the Configuration */
    static final String CONFIG_COMPONENT = EG_CONFIG_COMPONENT + ".admin";
    /** The component name for accessing the service's configuration */
    static String configComponent = CONFIG_COMPONENT;

    private final Logger logger = Logger.getLogger(getClass().getName());

    /**
     * Create a Web Administration Console launched from the ServiceStarter framework
     *
     * @param configArgs Configuration arguments
     * @param lifeCycle The LifeCycle object that started the Web Administration Console
     *
     * @throws Exception if bootstrapping fails
     */
    public AdminConsoleJSB(String[] configArgs, LifeCycle lifeCycle) throws Exception {
        super();
        bootstrap(configArgs);
    }

    /**
     * Get the ServiceBeanContext and bootstrap the REST API
     */
    protected void bootstrap(String[] configArgs) throws Exception {
        try {
            context = ServiceBeanActivation.getServiceBeanContext(
                                                  getConfigComponent(),
                                                  "Web Administration Console",
                                                  configArgs,
                                                  getClass().getClassLoader());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Getting ServiceElement", e);
            throw e;
        }
        try {
            start(context);
            ServiceBeanActivation.LifeCycleManager lMgr =
                    (ServiceBeanActivation.LifeCycleManager) context.getServiceBeanManager().getDiscardManager();
            if (lMgr != null) {
                lMgr.register(getServiceProxy(), context);
            } else {
                logger.log(Level.WARNING, "LifeCycleManager is null, unable to register");
            }
        } catch(Exception e) {
            logger.log(Level.SEVERE, "Register to LifeCycleManager", e);
            throw e;
        }
    }

    @Override
    public Object start(ServiceBeanContext context) throws Exception {
        logger.info("Starting Web Administration Console...");

        Configuration config = context.getConfiguration();
        Integer port = (Integer) config.getEntry("com.elasticgrid.admin", "port", Integer.class);
        String webappContext = (String) config.getEntry("com.elasticgrid.admin", "context", String.class);

        server = new Server(port);

        String egWebappsRoot = System.getProperty("EG_HOME") + File.separatorChar + "lib" + File.separatorChar + "elastic-grid";
        server.setHandler(new WebAppContext(egWebappsRoot + File.separatorChar + getWarName(egWebappsRoot,"admin-console"), webappContext));

//        ServletHandler handler=new ServletHandler();
//        server.setHandler(handler);
//        handler.addServletWithMapping("com.elasticgrid.admin.server.ClusterManagerServiceImpl", "/clusterManager");
//        handler.addServletWithMapping("com.elasticgrid.admin.server.StorageManagerServiceImpl", "/storageManager");

        server.start();
        server.join();
        return super.start(context);
    }

    @Override
    public void stop(boolean force) {
        super.stop(force);
        logger.info("Stopping Web Administration Console...");
        try {
            server.stop();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not stop properly Jetty server", e);
        }
    }

    /**
     * Get the component name to use for accessing the services configuration properties
     *
     * @return The component name
     */
    public static String getConfigComponent() {
        return configComponent;
    }

    private static String getWarName(String directory, String nameNoVersion) {
        String warName = null;
        File f = new File(directory);
        for (String s : f.list()) {
            if (s.startsWith(nameNoVersion)) {
                warName = s;
                break;
            }
        }
        return warName;
    }
}
