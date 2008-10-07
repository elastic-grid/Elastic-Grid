/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This file is part of Elastic Grid.
 * 
 * Elastic Grid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * 
 * Elastic Grid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Elastic Grid.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elasticgrid.rest;

import org.restlet.Application;
import org.restlet.Router;
import org.restlet.Restlet;
import org.restlet.Component;
import org.restlet.data.Protocol;
import com.elasticgrid.model.internal.Clusters;

public class RestApplication extends Application {

    @Override
    public synchronized Restlet createRoot() {
        Router router = new Router(getContext());
        router.attach("/eg", ClustersResource.class);
        router.attach("/eg/{clusterName}", ClusterResource.class);
        return router;
    }

    public static void main(String[] args) {
        try {
            // Create a new Component.
            Component component = new Component();

            // Add a new HTTP server listening on port 8182.
            component.getServers().add(Protocol.HTTP, 8182);

            // Attach the sample application.
            component.getDefaultHost().attach(new RestApplication());

            // Start the component.
            component.start();
        } catch (Exception e) {
            // Something is wrong.
            e.printStackTrace();
        }
    }

}
