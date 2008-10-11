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

public class ApplicationsResource extends WadlResource {
    private ClusterManager clusterManager;

    public ApplicationsResource(Context context, Request request, Response response) {
        super(context, request, response);
        // Allow modifications of this resource via POST requests
        setModifiable(true);
        // Declare the kind of representations supported by this resource
        getVariants().add(new Variant(MediaType.APPLICATION_XML));
    }

    /**
     * Handle GET requests: describe all applications.
     */
    @Override
    public Representation represent(Variant variant) throws ResourceException {
        return null;
    }

    /**
     * Handle PUT requests: provision a new application.
     */
    @Override
    public void storeRepresentation(Representation entity) throws ResourceException {
        super.acceptRepresentation(entity);
    }

    @Override
    protected void describeGet(MethodInfo info) {
        super.describeGet(info);
        info.setDocumentation("Describe all applications running on the cluster.");
        info.getResponse().setDocumentation("The applications.");
        RepresentationInfo representation = new RepresentationInfo();
        representation.setDocumentation("Applications");
        representation.setMediaType(MediaType.APPLICATION_XML);
        info.getResponse().setRepresentations(Arrays.asList(representation));
    }

    @Override
    protected void describePut(MethodInfo info) {
        super.describePost(info);
        info.setDocumentation("Provision a new application.");
        info.getRequest().setDocumentation("The application to provision.");
        RepresentationInfo representation = new RepresentationInfo();
        representation.setDocumentation("Application");
        representation.setMediaType(MediaType.APPLICATION_XML);
        info.getRequest().setRepresentations(Arrays.asList(representation));
    }

    @Override
    public boolean allowDelete() {
        return false;
    }

    @Override
    public boolean allowPost() {
        return false;
    }
}
