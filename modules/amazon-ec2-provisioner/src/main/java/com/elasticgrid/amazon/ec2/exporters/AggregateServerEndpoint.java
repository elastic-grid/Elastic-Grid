/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * Licensed under the GNU Lesser General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.elasticgrid.amazon.ec2.exporters;

import java.util.HashSet;
import java.util.Set;
import java.io.IOException;
import net.jini.io.UnsupportedConstraintException;
import net.jini.jeri.ServerEndpoint;
import net.jini.jeri.Endpoint;
import net.jini.core.constraint.InvocationConstraints;

/**
 * A {@link net.jini.jeri.ServerEndpoint} implementation that aggregates one or more
 * server endpoints and produces an {@link AggregateEndpoint} that contains
 * all of the endpoints produced by those server endpoints. When a remote
 * call is made through the aggregate endpoint, an attempt is made to
 * communicate through each of the contained endpoints in turn until one
 * succeeds.
 */
public final class AggregateServerEndpoint implements ServerEndpoint {
    /**
     * The contained server endpoints.
     */
    private final ServerEndpoint[] serverEndpoints;

    /**
     * Creates an instance with the specified server endpoints.
     */
    private AggregateServerEndpoint(ServerEndpoint[] serverEndpoints) {
        this.serverEndpoints = serverEndpoints;
    }

    /**
     * Returns an instance containing the specified server endpoints in the
     * specified order. The argument is neither modified nor retained;
     * subsequent changes to that argument have no effect on the instance
     * created.
     *
     * @param serverEndpoints the server endpoints to aggregate
     * @return an instance containing the specified server endpoints
     * @throws NullPointerException     if the argument or any element of it
     *                                  is <code>null</code>
     * @throws IllegalArgumentException if the argument is empty
     */
    public static AggregateServerEndpoint getInstance(
            ServerEndpoint[] serverEndpoints) {
        if (serverEndpoints.length == 0)
            throw new IllegalArgumentException();
        serverEndpoints = serverEndpoints.clone();
        for (int i = serverEndpoints.length; --i >= 0;) {
            if (serverEndpoints[i] == null)
                throw new NullPointerException();
        }
        return new AggregateServerEndpoint(serverEndpoints);
    }

    /**
     * Verifies that this instance supports the transport layer
     * aspects of all of the specified requirements (both in general
     * and in the current security context), and returns the
     * requirements that must be at least partially implemented by
     * higher layers in order to fully satisfy all of the specified
     * requirements.
     * <p/>
     * This method invokes the same method on each contained server endpoint
     * in turn, passing the specified constraints. Any runtime exception
     * thrown by such an invocation is rethrown by this method. If all of
     * those method invocations throw
     * <code>UnsupportedConstraintException</code>, then an
     * <code>UnsupportedConstraintException</code> is thrown by this method.
     * Otherwise, the constraints returned by this method contain the
     * intersection of all the requirements returned and the union of all
     * the preferences returned by those method invocations. That is, a
     * requirement is returned if and only if it was returned as a requirement
     * by all method invocations (that returned constraints), and a preference
     * is returned if it was returned by at least one method invocation.
     *
     * @throws SecurityException    {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    public InvocationConstraints checkConstraints(
            InvocationConstraints constraints)
            throws UnsupportedConstraintException {
        InvocationConstraints[] remain =
                new InvocationConstraints[serverEndpoints.length];
        int j = 0;
        for (int i = serverEndpoints.length; --i >= 0;) {
            try {
                remain[j] = serverEndpoints[i].checkConstraints(constraints);
                j++;
            } catch (UnsupportedConstraintException e) {
            }
        }
        if (j == 0) {
            throw new UnsupportedConstraintException(
                    "constraints not supported");
        }
        InvocationConstraints cs = remain[--j];
        Set reqs = new HashSet(cs.requirements());
        Set prefs = new HashSet(cs.preferences());
        while (--j >= 0) {
            cs = remain[j];
            reqs.retainAll(cs.requirements());
            prefs.addAll(cs.preferences());
        }
        return new InvocationConstraints(reqs, prefs);
    }

    /**
     * Enumerates the communication endpoints represented by this
     * <code>ServerEndpoint</code> by passing the
     * <code>ListenEndpoint</code> for each of them to
     * <code>listenContext</code>, which will ensure an active listen
     * operation on each endpoint, and returns an
     * <code>Endpoint</code> instance corresponding to the listen
     * operations chosen by <code>listenContext</code>.
     * <p/>
     * This method invokes the same method on each contained server endpoint,
     * passing the same argument, and returns an
     * {@link AggregateEndpoint} containing the resulting endpoints in
     * the same order as their corresponding server endpoints.
     *
     * @throws SecurityException        {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     * @throws NullPointerException     {@inheritDoc}
     */
    public Endpoint enumerateListenEndpoints(ListenContext listenContext) throws IOException {
        Endpoint[] endpoints = new Endpoint[serverEndpoints.length];
        for (int i = 0; i < serverEndpoints.length; i++) {
            endpoints[i] = serverEndpoints[i].enumerateListenEndpoints(listenContext);
        }
        return new AggregateEndpoint(endpoints);
    }

    /**
     * Returns a string representation of this instance.
     *
     * @return a string representation of this instance
     */
    public String toString() {
        StringBuffer buf = new StringBuffer("AggregateServerEndpoint[");
        for (int i = 0; i < serverEndpoints.length; i++) {
            if (i > 0)
                buf.append(", ");
            buf.append(serverEndpoints[i]);
        }
        buf.append(']');
        return buf.toString();
    }
}

