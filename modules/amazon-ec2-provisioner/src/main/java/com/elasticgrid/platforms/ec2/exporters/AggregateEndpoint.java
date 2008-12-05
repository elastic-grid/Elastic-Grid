/**
 * Elastic Grid
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
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

package com.elasticgrid.platforms.ec2.exporters;

import net.jini.core.constraint.InvocationConstraints;
import net.jini.jeri.Endpoint;
import net.jini.jeri.OutboundRequest;
import net.jini.jeri.OutboundRequestIterator;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;

/**
 * An {@link Endpoint} implementation that aggregates one or more endpoints.
 * When a remote call is made through the aggregate endpoint, an attempt is
 * made to communicate through each of the contained endpoints in turn until
 * one succeeds.
 */
public final class AggregateEndpoint implements Endpoint, Serializable {
    private static final long serialVersionUID = 2L;

    /**
     * @serialField endpoints Endpoint[] The contained endpoints.
     */
    private static final ObjectStreamField[] serialPersistentFields = {
            new ObjectStreamField("endpoints", Endpoint[].class, true)
    };

    /**
     * The contained endpoints.
     */
    private final Endpoint[] endpoints;

    /**
     * Creates an instance with the specified endpoints.
     */
    AggregateEndpoint(Endpoint[] endpoints) {
        this.endpoints = endpoints;
    }

    /**
     * Returns an instance containing the specified endpoints in the
     * specified order. The argument is neither modified nor retained;
     * subsequent changes to that argument have no effect on the instance
     * created.
     *
     * @param endpoints the endpoints to aggregate
     * @return an instance containing the specified endpoints
     * @throws NullPointerException     if the argument or any element of it
     *                                  is <code>null</code>
     * @throws IllegalArgumentException if the argument is empty
     */
    public static final AggregateEndpoint getInstance(Endpoint[] endpoints) {
        if (endpoints.length == 0) {
            throw new IllegalArgumentException();
        }
        endpoints = (Endpoint[]) endpoints.clone();
        for (int i = endpoints.length; --i >= 0;) {
            if (endpoints[i] == null) {
                throw new NullPointerException();
            }
        }
        return new AggregateEndpoint(endpoints);
    }

    /**
     * Returns an <code>OutboundRequestIterator</code> to use to send
     * a new request to this remote endpoint using the specified
     * constraints.
     * <p/>
     * The returned iterator operates by invoking the same
     * <code>newRequest</code> method on each contained endpoint in order,
     * passing the same constraints, and yields all of their outbound
     * requests in order.
     *
     * @throws NullPointerException {@inheritDoc}
     */
    public OutboundRequestIterator newRequest(final InvocationConstraints constraints) {
        if (constraints == null) {
            throw new NullPointerException();
        }
        return new OutboundRequestIterator() {
            private int i = 0;
            private OutboundRequestIterator iter = null;

            public boolean hasNext() {
                while ((iter == null || !iter.hasNext()) && i < endpoints.length) {
                    iter = endpoints[i++].newRequest(constraints);
                }
                return iter.hasNext();
            }

            public OutboundRequest next() throws IOException {
                hasNext();
                return iter.next();
            }
        };
    }

    /**
     * Returns a hash code value for this object.
     */
    public int hashCode() {
        int h = AggregateEndpoint.class.hashCode();
        for (int i = endpoints.length; --i >= 0;) {
            h += endpoints[i].hashCode();
        }
        return h;
    }

    /**
     * Two instances of this class are equal if they contain equivalent
     * endpoints in the same order. Two endpoints are considered equivalent
     * only if they have the same class and are equal.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof AggregateEndpoint)) {
            return false;
        }
        Endpoint[] oendpoints = ((AggregateEndpoint) obj).endpoints;
        if (endpoints.length != oendpoints.length) {
            return false;
        }
        for (int i = endpoints.length; --i >= 0;) {
            Endpoint e = endpoints[i];
            Endpoint oe = oendpoints[i];
            if (e.getClass() != oe.getClass() || !e.equals(oe)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a string representation of this instance.
     *
     * @return a string representation of this instance
     */
    public String toString() {
        StringBuffer buf = new StringBuffer("AggregateEndpoint[");
        for (int i = 0; i < endpoints.length; i++) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(endpoints[i]);
        }
        buf.append(']');
        return buf.toString();
    }

    /**
     * Verifies that there is at least one endpoint and that none of the
     * endpoints are <code>null</code>.
     *
     * @throws java.io.InvalidObjectException if there are no endpoints or
     *                                        or any endpoint is <code>null</code>.
     */
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        if (endpoints == null || endpoints.length == 0) {
            throw new InvalidObjectException("cannot create with no elements");
        }
        for (int i = endpoints.length; --i >= 0;) {
            if (endpoints[i] == null) {
                throw new InvalidObjectException("elements cannot be null");
            }
        }
    }
}
