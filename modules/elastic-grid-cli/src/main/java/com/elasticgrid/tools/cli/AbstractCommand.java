package com.elasticgrid.tools.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerome Bernard
 */
public abstract class AbstractCommand implements Command {
    protected Logger logger = LoggerFactory.getLogger(getClass());
}
