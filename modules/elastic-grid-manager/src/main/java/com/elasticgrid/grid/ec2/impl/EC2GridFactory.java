package com.elasticgrid.grid.ec2.impl;

import com.elasticgrid.model.ec2.EC2Grid;
import com.elasticgrid.model.ec2.impl.EC2GridImpl;
import com.elasticgrid.model.GridFactory;

/**
 * Grid factory for EC2.
 * @author Jerome Bernard
 */
public class EC2GridFactory implements GridFactory<EC2Grid> {
    public EC2Grid createGrid() {
        return new EC2GridImpl();
    }
}
