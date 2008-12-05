/**
 * Elastic Grid
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
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

package com.elasticgrid.rest;

import org.rioproject.core.jsb.ServiceBeanContext;
import org.rioproject.jsb.ServiceBeanAdapter;
import org.rioproject.jsb.ServiceBeanActivation;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.sun.jini.start.LifeCycle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JSB exposing the underlying REST API.
 */
public class RestJSB extends ServiceBeanAdapter {
    private LifeCycle lifeCycle;
    private ConfigurableApplicationContext springContext;
    /** Component name we use to find items in the Configuration */
    static final String RIO_CONFIG_COMPONENT = "com.elasticgrid";
    /** Component name we use to find items in the Configuration */
    static final String CONFIG_COMPONENT = RIO_CONFIG_COMPONENT + ".rest";
    /** The componant name for accessing the service's configuration */
    static String configComponent = CONFIG_COMPONENT;
    /** Logger name */
    static final String LOGGER = "org.rioproject.cybernode";
    /** Cybernode logger. */
    static final Logger logger = Logger.getLogger(LOGGER);

    /**
     * Create a REST API launched from the ServiceStarter framework
     *
     * @param configArgs Configuration arguments
     * @param lifeCycle The LifeCycle object that started the REST API
     *
     * @throws Exception if bootstrapping fails
     */
    public RestJSB(String[] configArgs, LifeCycle lifeCycle) throws Exception {
        super();
        this.lifeCycle = lifeCycle;
        bootstrap(configArgs);
    }

    /*
     * Get the ServiceBeanContext and bootstrap the REST API
     */
    protected void bootstrap(String[] configArgs) throws Exception {
        try {
            context = ServiceBeanActivation.getServiceBeanContext(
                                                  getConfigComponent(),
                                                  "REST",
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

    /**
     * Get the component name to use for accessing the services configuration properties
     *
     * @return The component name
     */
    public static String getConfigComponent() {
        return configComponent;
    }

    @Override
    public void initialize(ServiceBeanContext context) throws Exception {
        super.initialize(context);
        springContext = new ClassPathXmlApplicationContext("/com/elasticgrid/rest/applicationContext.xml");
    }

    @Override
    public void destroy(boolean force) {
        if (springContext != null)
            springContext.close();
        super.destroy(force);
    }
}
