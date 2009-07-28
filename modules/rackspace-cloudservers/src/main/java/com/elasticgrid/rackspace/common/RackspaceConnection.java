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
package com.elasticgrid.rackspace.common;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpHost;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HeaderElement;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.protocol.HttpContext;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.commons.io.IOUtils;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IUnmarshallingContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * This class provides common code to the REST connection classes
 *
 * @author Jerome Bernard
 */
public class RackspaceConnection {
    // this is the number of automatic retries
    private int maxRetries = 5;
    private String userAgent = "Elastic-Grid/";
    private HttpClient hc = null;
    private int maxConnections = 100;
    private String proxyHost = null;
    private int proxyPort;
    private int connectionManagerTimeout = 0;
    private int soTimeout = 0;
    private int connectionTimeout = 0;

    private final String username;
    private final String apiKey;
    private String serverManagementURL;
    private String storageURL;
    private String cdnManagementURL;
    private String authToken;

    private boolean authenticated = false;

    private static final String API_AUTH_URL = "https://auth.api.rackspacecloud.com/v1.0";

    private static final Logger logger = Logger.getLogger(RackspaceConnection.class.getName());

    /**
     * Initializes the Rackspace connection with the Rackspace login information.
     *
     * @param username the Rackspace username
     * @param apiKey   the Rackspace API key
     */
    public RackspaceConnection(String username, String apiKey) throws RackspaceException, IOException {
        this.username = username;
        this.apiKey = apiKey;
        String version = "?";
        try {
            Properties props = new Properties();
            props.load(this.getClass().getClassLoader().getResourceAsStream("version.properties"));
            version = props.getProperty("version");
        } catch (Exception ex) {
        }
        userAgent = userAgent + version + " (" + System.getProperty("os.arch") + "; " + System.getProperty("os.name") + ")";
        authenticate();
    }

    /**
     * Authenticate on Rackspace API.
     * Tokens are only valid for 24 hours, so client code should expect token to expire and renew them if needed.
     *
     * @return the auth token, valid for 24 hours
     * @throws RackspaceException if the credentials are invalid
     * @throws IOException        if there is a network issue
     */
    public String authenticate() throws RackspaceException, IOException {
        logger.info("Authenticating to Rackspace API...");
        HttpGet request = new HttpGet(API_AUTH_URL);
        request.addHeader("X-Auth-User", username);
        request.addHeader("X-Auth-Key", apiKey);
        request.addHeader(CoreProtocolPNames.USER_AGENT, userAgent);
        HttpResponse response = getHttpClient().execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        switch (statusCode) {
            case 204:
                if (response.getFirstHeader("X-Server-Management-Url") != null)
                    serverManagementURL = response.getFirstHeader("X-Server-Management-Url").getValue();
                if (response.getFirstHeader("X-Storage-Url") != null)
                    storageURL = response.getFirstHeader("X-Storage-Url").getValue();
                if (response.getFirstHeader("X-CDN-Management-Url") != null)
                    cdnManagementURL = response.getFirstHeader("X-CDN-Management-Url").getValue();
                authToken = response.getFirstHeader("X-Auth-Token").getValue();
                authenticated = true;
                return authToken;
            case 401:
                throw new RackspaceException("Invalid credentials: " + response.getStatusLine().getReasonPhrase());
            default:
                throw new RackspaceException("Unexpected HTTP response");
        }
    }

    /**
     * Make a http request and process the response. This method also performs automatic retries.
     *
     * @param request  the HTTP method to use (GET, POST, DELETE, etc)
     * @param respType the class that represents the desired/expected return type
     * @return the unmarshalled entity
     */
    protected <T> T makeRequest(HttpRequestBase request, Class<T> respType)
            throws HttpException, IOException, JiBXException, RackspaceException {

        if (!authenticated)
            authenticate();

        // add auth params, and protocol specific headers
        request.addHeader("X-Auth-Token", getAuthToken());
        request.addHeader(CoreProtocolPNames.USER_AGENT, userAgent);

        // set accept and content-type headers
        request.setHeader("Accept", "application/xml; charset=UTF-8");
        request.setHeader("Accept-Encoding", "gzip");
        request.setHeader("Content-Type", "application/xml; charset=UTF-8");

        // send the request
        T result;
        boolean done = false;
        int retries = 0;
        boolean doRetry = false;
        RackspaceException error = null;
        do {
            int statusCode = 600;
            HttpResponse response = null;
            try {
                logger.log(Level.INFO, "Querying {0}", request.getURI());
                response = getHttpClient().execute(request);
                statusCode = response.getStatusLine().getStatusCode();
            } catch (SocketException e) {
                doRetry = true;
                error = new RackspaceException(e.getMessage(), e);
            }
            if (statusCode < 300) {
                // 200: normal response
            }
            done = true;

            InputStream entityStream = null;
            try {
                HttpEntity entity = response.getEntity();
                entityStream = entity.getContent();
                IBindingFactory bindingFactory = BindingDirectory.getFactory(respType);
                IUnmarshallingContext unmarshallingCxt = bindingFactory.createUnmarshallingContext();
                result = (T) unmarshallingCxt.unmarshalDocument(entityStream, "UTF-8");
            } finally {
                IOUtils.closeQuietly(entityStream);
            }

            if (doRetry) {
                retries++;
                if (retries > maxRetries) {
                    throw new HttpException("Number of retries exceeded for " + request.getURI(), error);
                }
                doRetry = false;
                try {
                    Thread.sleep((int) Math.pow(2.0, retries) * 1000);
                } catch (InterruptedException ex) {
                }
            }
        } while (!done);

        return result;

        /*
        Object response = null;
        boolean done = false;
        int retries = 0;
        boolean doRetry = false;
        AWSException error = null;
        do {
            int responseCode = 600;    // default to high value, so we don't think it is valid
            try {
                responseCode = getHttpClient().executeMethod(request);
            } catch (SocketException ex) {
                // these can generally be retried. Treat it like a 500 error
                doRetry = true;
                error = new AWSException(ex.getMessage(), ex);
            }
            // 100's are these are handled by httpclient
            if (responseCode < 300) {
                // 200's : parse normal response into requested object
                if (respType != null) {
                    InputStream iStr = request.getResponseBodyAsStream();
                    response = JAXBuddy.deserializeXMLStream(respType, iStr);
                }
                done = true;
            } else if (responseCode < 400) {
                // 300's : what to do?
                throw new HttpException("redirect error : " + responseCode);
            } else if (responseCode < 500) {
                // 400's : parse client error message
                String body = getStringFromStream(request.getResponseBodyAsStream());
                throw createException(body, "Client error : ");
            } else if (responseCode < 600) {
                // 500's : retry...
                doRetry = true;
                String body = getStringFromStream(request.getResponseBodyAsStream());
                error = createException(body, "");
            }
            if (doRetry) {
                retries++;
                if (retries > maxRetries) {
                    throw new HttpException("Number of retries exceeded : " + action, error);
                }
                doRetry = false;
                try {
                    Thread.sleep((int) Math.pow(2.0, retries) * 1000);
                } catch (InterruptedException ex) {
                }
            }
        } while (!done);
        return (T) response;
        */
    }

    private void configureHttpClient() {
        HttpParams params = new BasicHttpParams();
//        params.setBooleanParameter("http.tcp.nodelay", true);
//        params.setBooleanParameter("http.coonection.stalecheck", false);
        ConnManagerParams.setTimeout(params, getConnectionManagerTimeout());
        ConnManagerParams.setMaxTotalConnections(params, getMaxConnections());
//        params.setIntParameter("http.soTimeout", getSoTimeout());     // TODO: figure out the param name!

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager connMgr = new ThreadSafeClientConnManager(params, schemeRegistry);
        hc = new DefaultHttpClient(connMgr, params);

        ((DefaultHttpClient) hc).addRequestInterceptor(new HttpRequestInterceptor() {
            public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                if (!request.containsHeader("Accept-Encoding")) {
                    request.addHeader("Accept-Encoding", "gzip");
                }
            }
        });
        ((DefaultHttpClient) hc).addResponseInterceptor(new HttpResponseInterceptor() {
            public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
                HttpEntity entity = response.getEntity();
                if (entity == null)
                    return;
                Header ceHeader = entity.getContentEncoding();
                if (ceHeader != null) {
                    for (HeaderElement codec : ceHeader.getElements()) {
                        if (codec.getName().equalsIgnoreCase("gzip")) {
                            response.setEntity(new GzipDecompressingEntity(response.getEntity()));
                            return;
                        }
                    }
                }
            }
        });

        if (proxyHost != null) {
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            hc.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
            logger.info("Proxy Host set to " + proxyHost + ":" + proxyPort);
        }
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getServerManagementURL() {
        return serverManagementURL;
    }

    public String getStorageURL() {
        return storageURL;
    }

    public String getCdnManagementURL() {
        return cdnManagementURL;
    }

    protected HttpClient getHttpClient() {
        if (hc == null) {
            configureHttpClient();
        }
        return hc;
    }

    public void setHttpClient(HttpClient hc) {
        this.hc = hc;
    }

    public int getConnectionManagerTimeout() {
        return connectionManagerTimeout;
    }

    public void setConnectionManagerTimeout(int timeout) {
        connectionManagerTimeout = timeout;
        hc = null;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public void setSoTimeout(int timeout) {
        soTimeout = timeout;
        hc = null;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int timeout) {
        connectionTimeout = timeout;
        hc = null;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    static class GzipDecompressingEntity extends HttpEntityWrapper {

        public GzipDecompressingEntity(final HttpEntity entity) {
            super(entity);
        }

        @Override
        public InputStream getContent() throws IOException, IllegalStateException {
            // the wrapped entity's getContent() decides about repeatability
            InputStream wrappedin = wrappedEntity.getContent();
            return new GZIPInputStream(wrappedin);
        }

        @Override
        public long getContentLength() {
            // length of ungzipped content is not known
            return -1;
        }

    }
}
