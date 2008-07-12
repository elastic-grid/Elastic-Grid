package com.elasticgrid.grid.ec2;

import static java.lang.String.format;

/**
 * Exception thrown when a grid can't be found
 * @author Jerome Bernard
 */
public class GridNotFoundException extends Exception {
    public GridNotFoundException(String gridName) {
        super(format("Could not find grid '%s'", gridName));
    }
}
