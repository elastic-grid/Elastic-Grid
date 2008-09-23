package com.elasticgrid.tools.ui;

import com.elasticgrid.utils.amazon.AWSUtils;
import com.elasticgrid.grid.GridManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    private GridManager gridManager;

    public Main() {
        System.out.println("EG_HOME = " + System.getenv("EG_HOME"));
        if (!AWSUtils.isEnvironmentProperlySet()) {
            System.err.println("Missing eg.properties file in $EG_HOME/config");
            System.exit(-1);    // todo: instead provide a UI for entering this information and keep going
        }
        // setup Spring Application Context
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/com/elasticgrid/grid/ec2/applicationContext.xml");
        gridManager = (GridManager) ctx.getBean("gridManager", GridManager.class);
        // build the initial UI
        new MainPanel();
    }

    public static void main(String[] args) {
        new Main();
    }
}
