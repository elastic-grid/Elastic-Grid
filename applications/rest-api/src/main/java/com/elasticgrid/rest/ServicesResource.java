package com.elasticgrid.rest;

import com.elasticgrid.cluster.ClusterManager;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.WadlResource;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import java.util.Arrays;
import java.util.HashMap;

public class ServicesResource extends WadlResource {
    private ClusterManager clusterManager;

    public ServicesResource(Context context, Request request, Response response) {
        super(context, request, response);
        // Allow modifications of this resource via POST requests
        setModifiable(false);
        // Declare the kind of representations supported by this resource
        getVariants().add(new Variant(MediaType.APPLICATION_XML));
    }

    /**
     * Handle GET requests: describe all services.
     */
    @Override
    public Representation represent(Variant variant) throws ResourceException {
        return null;
    }

    @Override
    protected void describeGet(MethodInfo info) {
        super.describeGet(info);
        info.setDocumentation("Describe all services of application {applicationName} running on cluster {clusterName}.");
        info.getResponse().setDocumentation("The services.");
        RepresentationInfo representation = new RepresentationInfo();
        representation.setDocumentation("Services");
        representation.setMediaType(MediaType.APPLICATION_XML);
        info.getResponse().setRepresentations(Arrays.asList(representation));
    }

}