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
import com.elasticgrid.model.internal.Applications;
import com.elasticgrid.utils.amazon.AWSUtils;
import com.elasticgrid.storage.StorageManager;
import com.elasticgrid.storage.Container;
import com.elasticgrid.storage.StorageException;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.ext.jibx.JibxRepresentation;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.WadlResource;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplicationsResource extends WadlResource {
    private String clusterName;
    private String dropBucket;
    private Configuration config;
    private ClusterManager clusterManager;
    private StorageManager storageManager;
    private final Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public void init(Context context, Request request, Response response) {
        super.init(context, request, response);
        clusterManager = RestJSB.getClusterManager();
        storageManager = RestJSB.getStorageManager();
        try {
            dropBucket = AWSUtils.getDropBucket();
        } catch (IOException e) {
            throw new IllegalStateException("Can't retrieve drop bucket", e);
        }
        // Allow modifications of this resource via POST requests
        setModifiable(true);
        // Declare the kind of representations supported by this resource
        getVariants().add(new Variant(MediaType.APPLICATION_XML));
        // Extract URI variables
        clusterName = (String) request.getAttributes().get("clusterName");
        // Setup FreeMarker template engine
        config = new Configuration();
        config.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
        config.setTemplateLoader(new ClassTemplateLoader(getClass(), "/com/elasticgrid/rest"));
    }

    /**
     * Handle GET requests: describe all applications.
     */
    @Override
    public Representation represent(Variant variant) throws ResourceException {
        try {
            logger.log(Level.INFO, "Requested variant {0}", variant.getMediaType());
            Cluster cluster = clusterManager.cluster(clusterName);
            if (cluster == null)
                throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "Can't find cluster " + clusterName);
            logger.log(Level.INFO, "Found cluster {0}", cluster);

            if (MediaType.TEXT_HTML.equals(variant.getMediaType())
                    || MediaType.APPLICATION_XML.equals(variant.getMediaType())) {
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("cluster", cluster);
                return new TemplateRepresentation("applications.ftl", config, model, MediaType.TEXT_HTML);
            } else {
                // return XML representation
                return new JibxRepresentation<Applications>(MediaType.APPLICATION_XML,
                        new Applications(cluster.getApplications()), "ElasticGridREST");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE, e);
        }
    }

    /**
     * Handle POST requests: provision a new application.
     */
    @Override
    public void acceptRepresentation(Representation entity) throws ResourceException {
        super.acceptRepresentation(entity);    //To change body of overridden methods use File | Settings | File Templates.

        if (MediaType.MULTIPART_ALL.equals(entity.getMediaType(), true)
                || MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)) {
            try {
                DiskFileItemFactory factory = new DiskFileItemFactory();
                factory.setSizeThreshold(1000240);
                RestletFileUpload upload = new RestletFileUpload(factory);
                List<FileItem> files = upload.parseRequest(getRequest());

                logger.log(Level.INFO, "Found {0} items", files.size());
                for (FileItem fi : files) {
                    if ("oar".equals(fi.getFieldName())) {
                        // download it as a temp file
                        File file = File.createTempFile("elastic-grid", "oar");
                        fi.write(file);
                        // upload it to storage container
                        logger.log(Level.INFO, "Uploading OAR ''{0}'' to {1}'s container ''{2}''",
                                new Object[]{fi.getName(), storageManager.getStorageName(), dropBucket});
                        Container container = storageManager.findContainerByName(dropBucket);
                        try {
                            container.uploadStorable(file);
                        } catch (StorageException e) {
                            logger.log(Level.SEVERE,
                                    String.format("Could not upload OAR '%s' to %s's container '%s'",
                                            fi.getName(), storageManager.getStorageName(), dropBucket), e);
                            throw new ResourceException(Status.SERVER_ERROR_INSUFFICIENT_STORAGE, e);
                        }
                    }
                }

                // Set the status of the response.
                logger.info("Redirecting to " + getRequest().getOriginalRef());
                getResponse().setLocationRef(getRequest().getOriginalRef().addSegment("??"));  // todo: figure out the proper URL
            } catch (FileUploadException e) {
                e.printStackTrace();
                throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
            } catch (Exception e) {
                e.printStackTrace();
                throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
            }
        } else if (new MediaType("application/oar").equals(entity.getMediaType())) {
            try {
                // extract filename information
                Form form = (Form) getRequest().getAttributes().get("org.restlet.http.headers");
                // upload it to storage container
                String fileName = form.getFirstValue("x-filename");
                logger.log(Level.INFO, "Uploading OAR ''{0}'' to {1}'s container ''{2}''",
                        new Object[]{fileName, storageManager.getStorageName(), dropBucket});
                Container container = storageManager.findContainerByName(dropBucket);
                try {
                    container.uploadStorable(fileName, entity.getStream(), "application/oar");
                } catch (StorageException e) {
                    logger.log(Level.SEVERE,
                            String.format("Could not upload OAR '%s' to %s's container '%s'",
                                    fileName, storageManager.getStorageName(), dropBucket), e);
                    throw new ResourceException(Status.SERVER_ERROR_INSUFFICIENT_STORAGE, e);
                }
                // Set the status of the response
                logger.info("Redirecting to " + getRequest().getOriginalRef());
                getResponse().setLocationRef(getRequest().getOriginalRef().addSegment("??"));  // todo: figure out the proper URL
            } catch (Exception e) {
                e.printStackTrace();
                throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
            }
        } else {
            throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
        }
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
    protected void describePost(MethodInfo info) {
        super.describePost(info);
        info.setDocumentation("Provision a new application on {clusterName}.");
        info.getRequest().setDocumentation("The application to provision packaged as an OAR.");
        RepresentationInfo formRepresentation = new RepresentationInfo();
        formRepresentation.setDocumentation("HTML form with file uploads.");
        formRepresentation.setMediaType(MediaType.MULTIPART_FORM_DATA);
        info.getRequest().setRepresentations(Arrays.asList(formRepresentation));
    }

    @Override
    public boolean allowPut() {
        return false;
    }

    @Override
    public boolean allowDelete() {
        return false;
    }
}
