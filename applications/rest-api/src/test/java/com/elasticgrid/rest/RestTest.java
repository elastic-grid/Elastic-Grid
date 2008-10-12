package com.elasticgrid.rest;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {
        "/com/elasticgrid/rest/applicationContext.xml"
})
public class RestTest extends AbstractTestNGSpringContextTests {

    @Test
    public void testRestAPI() {
        // do nothing
    }

}
