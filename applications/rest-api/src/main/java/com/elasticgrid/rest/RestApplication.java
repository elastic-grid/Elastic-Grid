package com.elasticgrid.rest;

import org.restlet.data.Protocol;
import org.restlet.ext.spring.SpringComponent;
import org.restlet.ext.wadl.WadlApplication;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RestApplication extends WadlApplication implements InitializingBean {

    public void afterPropertiesSet() throws Exception {
        try {
            // Create a new Component.
            SpringComponent component = new SpringComponent();

            // Add a new HTTP server listening on port 8182.
            component.getServers().add(Protocol.HTTP, 8182);

            // Attach the sample application.
            component.getDefaultHost().attach(this);

            // Start the component.
            component.start();
        } catch (Exception e) {
            // Something is wrong.
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/com/elasticgrid/rest/applicationContext.xml");
    }

}
