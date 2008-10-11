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

import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Router;
import org.restlet.data.Protocol;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Reference;
import org.restlet.ext.wadl.WadlApplication;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.GrammarsInfo;
import org.restlet.ext.wadl.IncludeInfo;
import org.restlet.service.TunnelService;
import com.noelios.restlet.ext.jetty.HttpServerHelper;
import java.util.HashMap;

public class RestApplication extends WadlApplication {

    @Override
    public synchronized Restlet createRoot() {
        Router router = new Router(getContext());
        router.attach("/eg", ClustersResource.class);
        router.attach("/eg/{clusterName}", ClusterResource.class);
        router.attach("/eg/{clusterName}/applications", ApplicationsResource.class);
        router.attach("/eg/{clusterName}/applications/{applicationName}", ApplicationResource.class);
        router.attach("/eg/{clusterName}/applications/{applicationName}/services", ServicesResource.class);
        router.attach("/eg/{clusterName}/applications/{applicationName}/services/{serviceName}", ServiceResource.class);
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

            // Start the tunnel
            TunnelService tunnel = new TunnelService(true, true);
            tunnel.start();
        } catch (Exception e) {
            // Something is wrong.
            e.printStackTrace();
        }
    }

//    @Override
//    public ApplicationInfo getApplicationInfo(Request request, Response response) {
//        ApplicationInfo applicationInfo = super.getApplicationInfo(request, response);
//        GrammarsInfo grammarsInfo = new GrammarsInfo();
//        IncludeInfo includeInfo = new IncludeInfo();
//        includeInfo.setTargetRef(new Reference("http://www.elastic-grid.com/schema/elastic-grid-0.8.2.xsd"));
//        grammarsInfo.getIncludes().add(includeInfo);
//        applicationInfo.setGrammars(grammarsInfo);
//        return applicationInfo;
//    }

    @Override
    public String getName() {
        return "Elastic Grid REST API";
    }

    @Override
    public String getOwner() {
        return "Elastic Grid, LLC.";
    }

    @Override
    public String getAuthor() {
        return "Elastic Grid, LLC.";
    }

    @Override
    public String getDescription() {
        return "Elastic Grid REST API";
    }
}
