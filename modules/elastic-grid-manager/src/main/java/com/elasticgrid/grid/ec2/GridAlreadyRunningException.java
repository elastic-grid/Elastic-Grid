package com.elasticgrid.grid.ec2;

import com.elasticgrid.model.Grid;
import static java.lang.String.format;

/**
 * Exception thrown when trying to start an already running grid.
 * @author Jerome Bernard
 */
public class GridAlreadyRunningException extends Exception {
    public GridAlreadyRunningException(Grid runningGrid) {
        super(format("Can't start grid '%s' because it is already running.", runningGrid.getName()));
    }
}
