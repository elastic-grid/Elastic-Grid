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

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpConnection;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHeader;
import org.jibx.runtime.JiBXException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import java.util.logging.Logger;
import java.util.Properties;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Date;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.ByteArrayInputStream;
import java.text.Collator;
import java.text.SimpleDateFormat;

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
    private String proxyUser;
    private String proxyPassword;
    private String proxyDomain;    // for ntlm authentication
    private int connectionManagerTimeout = 0;
    private int soTimeout = 0;
    private int connectionTimeout = 0;

    private final String username;
    private final String apiKey;
    private String serverManagementURL;
    private String storageURL;
    private String cdnManagementURL;
    private String authToken;

    private static final String API_AUTH_URL = "https://auth.api.rackspacecloud.com/v1.0";

    private static final Logger logger = Logger.getLogger(RackspaceConnection.class.getName());

    /**
     * Initializes the Rackspace connection with the Rackspace login information.
     *
     * @param username the Rackspace username
     * @param apiKey   the Rackspace API key
     */
    public RackspaceConnection(String username, String apiKey) {
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
    }

    /**
     * Authenticate on Rackspace API.
     * Tokens are only valid for 24 hours, so client code should expect token to expire and renew them if needed.
     * @return the auth token, valid for 24 hours
     * @throws RackspaceException if the credentials are invalid
     * @throws IOException if there is a network issue
     */
    public String authenticate() throws RackspaceException, IOException {
        HttpGet request = new HttpGet();
        request.addHeader("X-Auth-User", username);
        request.addHeader("X-Auth-Key", apiKey);
        HttpResponse response = hc.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        switch (statusCode) {
            case 204:
                serverManagementURL = response.getFirstHeader("X-Server-Management-Url").getValue();
                storageURL = response.getFirstHeader("X-Storage-Url").getValue();
                cdnManagementURL = request.getFirstHeader("X-CDN-Management-Url").getValue();
                authToken = request.getFirstHeader("X-Auth-Token").getValue();
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
     * @param request   the HTTP method to use (GET, POST, DELETE, etc)
     * @param respType  the class that represents the desired/expected return type
     */
    protected <T> T makeRequest(HttpRequestBase request, Class<T> respType)
            throws HttpException, IOException, JiBXException, RackspaceException {

        // add auth params, and protocol specific headers
        request.addHeader("X-Auth-Token", getAuthToken());
        request.addHeader(CoreProtocolPNames.USER_AGENT, userAgent);

        // set accept and content-type headers
        request.setHeader("Accept", "application/xml; charset=UTF-8");
        request.setHeader("Content-Type", "application/xml; charset=UTF-8");

        return null;        // TODO: write the real code!

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
        /*
        MultiThreadedHttpConnectionManager connMgr = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams connParams = connMgr.getParams();
        connParams.setMaxTotalConnections(maxConnections);
        connParams.setMaxConnectionsPerHost(HostConfiguration.ANY_HOST_CONFIGURATION, maxConnections);
        connParams.setConnectionTimeout(connectionTimeout);
        connParams.setSoTimeout(soTimeout);
        connMgr.setParams(connParams);
        hc = new HttpClient(connMgr);
// NOTE: These didn't seem to help in my initial testing
//			hc.getParams().setParameter("http.tcp.nodelay", true);
//			hc.getParams().setParameter("http.connection.stalecheck", false);
        hc.getParams().setConnectionManagerTimeout(connectionManagerTimeout);
        hc.getParams().setSoTimeout(soTimeout);
        if (proxyHost != null) {
            HostConfiguration hostConfig = new HostConfiguration();
            hostConfig.setProxy(proxyHost, proxyPort);
            hc.setHostConfiguration(hostConfig);
            log.info("Proxy Host set to " + proxyHost + ":" + proxyPort);
            if (proxyUser != null && !proxyUser.trim().equals("")) {
                if (proxyDomain != null) {
                    hc.getState().setProxyCredentials(new AuthScope(proxyHost, proxyPort),
                            new NTCredentials(proxyUser, proxyPassword, proxyHost, proxyDomain));
                } else {
                    hc.getState().setProxyCredentials(new AuthScope(proxyHost, proxyPort),
                            new UsernamePasswordCredentials(proxyUser, proxyPassword));
                }
            }
        }
        */
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

    private String getStringFromStream(InputStream iStr) throws IOException {
        InputStreamReader rdr = new InputStreamReader(iStr, "UTF-8");
        StringWriter wtr = new StringWriter();
        char[] buf = new char[1024];
        int bytes;
        while ((bytes = rdr.read(buf)) > -1) {
            if (bytes > 0) {
                wtr.write(buf, 0, bytes);
            }
        }
        iStr.close();
        return wtr.toString();
    }


    /**
     * Generate an rfc822 date for use in the Date HTTP header.
     */
    private static String httpDate() {
        final String DateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat format = new SimpleDateFormat(DateFormat, Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(new Date());
    }

    /**
     * @return connection manager timeout in milliseconds
     * @see org.apache.commons.httpclient.params.HttpClientParams.getConnectionManagerTimeout()
     */
    public int getConnectionManagerTimeout() {
        return connectionManagerTimeout;
    }

    /**
     * @param connection manager timeout in milliseconds
     * @see org.apache.commons.httpclient.params.HttpClientParams.getConnectionManagerTimeout()
     */
    public void setConnectionManagerTimeout(int timeout) {
        connectionManagerTimeout = timeout;
        hc = null;
    }

    /**
     * @return socket timeout in milliseconds
     * @see org.apache.commons.httpclient.params.HttpConnectionParams.getSoTimeout()
     * @see org.apache.commons.httpclient.params.HttpMethodParams.getSoTimeout()
     */
    public int getSoTimeout() {
        return soTimeout;
    }

    /**
     * @param socket timeout in milliseconds
     * @see org.apache.commons.httpclient.params.HttpConnectionParams.getSoTimeout()
     * @see org.apache.commons.httpclient.params.HttpMethodParams.getSoTimeout()
     */
    public void setSoTimeout(int timeout) {
        soTimeout = timeout;
        hc = null;
    }

    /**
     * @return connection timeout in milliseconds
     * @see org.apache.commons.httpclient.params.HttpConnectionParams.getConnectionTimeout()
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * @param connection timeout in milliseconds
     * @see org.apache.commons.httpclient.params.HttpConnectionParams.getConnectionTimeout()
     */
    public void setConnectionTimeout(int timeout) {
        connectionTimeout = timeout;
        hc = null;
    }

    public String getUsername() {
        return username;
    }
}
