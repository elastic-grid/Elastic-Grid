/**
 * Elastic Grid
 * Copyright (C) 2008-2009 Elastic Grid, LLC.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elasticgrid.rest;

import com.elasticgrid.cluster.ClusterManager;
import com.elasticgrid.model.Cluster;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.WadlResource;
import org.restlet.ext.jibx.JibxRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.restlet.resource.StringRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.rioproject.core.OperationalStringManager;
import org.rioproject.core.OperationalString;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class ApplicationsResource extends WadlResource {
    @Autowired
    private ClusterManager clusterManager;

    @Override
    public void init(Context context, Request request, Response response) {
        super.init(context, request, response);
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
        try {
            List<OperationalStringManager> opstringMgrs = RestJSB.getOperationalStringManagers();
            for (OperationalStringManager opstringMgr : opstringMgrs) {
                OperationalString opstring = opstringMgr.getOperationalString();
                Logger.getLogger(getClass().getName()).info(opstring.getName());
            }
            return new StringRepresentation("so???");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE, e);
        }
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
        info.setDocumentation("Describe all applications running on the cluster {clusterName}.");
        info.getResponse().setDocumentation("The cluster.");
        RepresentationInfo representation = new RepresentationInfo();
        representation.setDocumentation("This resource exposes applications running on cluster {clusterName}.");
        representation.getDocumentations().get(0).setTitle("applications");
        representation.setMediaType(MediaType.APPLICATION_XML);
        representation.getDocumentations().addAll(Arrays.asList(
                new DocumentationInfo("Example of output:<pre><![CDATA[" +
                        "<applications xmlns=\"urn:elastic-grid:eg\">\n" +
                        "  <application name=\"myapp\" cluster=\"cluster2\">\n" +
                        "    <service name=\"My First Service\">\n" +
                        "      <provisioning planned=\"1\" deployed=\"1\" pending=\"0\"/>\n" +
                        "    </service>\n" +
                        "    <service name=\"My Second Service\">\n" +
                        "      <provisioning planned=\"1\" deployed=\"1\" pending=\"0\"/>\n" +
                        "    </service>\n" +
                        "  </application>\n" +
                        "</applications>" +
                        "]]></pre>")
        ));
        representation.setXmlElement("eg:applications");
        info.getResponse().setRepresentations(Arrays.asList(representation));
    }

    @Override
    protected void describePut(MethodInfo info) {
        super.describePut(info);
        info.setDocumentation("Provision a new application on {clusterName}.");
        info.getRequest().setDocumentation("The application to provision.");
        RepresentationInfo xmlRepresentation = new RepresentationInfo();
        xmlRepresentation.setDocumentation("This representation exposes an OpString in XML format.");
        xmlRepresentation.getDocumentations().get(0).setTitle("OpString in XML format");
        xmlRepresentation.setMediaType(MediaType.APPLICATION_XML);
        RepresentationInfo dslReprentation = new RepresentationInfo();
        dslReprentation.setDocumentation("This representation exposes an OpString in DSL format.");
        dslReprentation.getDocumentations().get(0).setTitle("OpString in DSL format");
        dslReprentation.setMediaType(new MediaType("text/x-groovy", "Groovy"));
        info.getRequest().setRepresentations(Arrays.asList(xmlRepresentation, dslReprentation));
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
