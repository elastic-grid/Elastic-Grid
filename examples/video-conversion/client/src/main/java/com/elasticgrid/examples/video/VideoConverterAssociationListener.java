/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This file is part of Elastic Grid.
 * 
 * Elastic Grid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * Elastic Grid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Elastic Grid.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elasticgrid.examples.video;

import org.rioproject.associations.Association;
import org.rioproject.associations.AssociationListener;

public class VideoConverterAssociationListener implements AssociationListener {
    public void discovered(Association association, Object service) {
        if (service instanceof VideoConverter) {
            Converter.converter = (VideoConverter) service;
        } else {
            System.out.printf("Found %s instead\n", service.getClass().getName());
        }
    }

    public void changed(Association association, Object service) {}

    public void broken(Association association, Object service) {
        if (service instanceof VideoConverter)
            Converter.converter = null;
    }
}