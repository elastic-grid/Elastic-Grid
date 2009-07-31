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
package com.elasticgrid.rackspace.cloudservers;

import java.util.List;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.io.Serializable;

/**
 * Rackspace API limits.
 * @author Jerome Bernard
 */
public class Limits implements Serializable {
    private final List<RateLimit> rateLimits;
    private final List<AbsoluteLimit> absoluteLimits;

    public Limits(List<RateLimit> rateLimits, List<AbsoluteLimit> absoluteLimits) {
        this.rateLimits = Collections.unmodifiableList(rateLimits);
        this.absoluteLimits = Collections.unmodifiableList(absoluteLimits);
    }

    public List<RateLimit> getRateLimits() {
        return rateLimits;
    }

    public List<AbsoluteLimit> getAbsoluteLimits() {
        return absoluteLimits;
    }

}

class RateLimit implements Serializable {
    private final HTTPVerb verb;
    private final String URI;
    private final String regex;
    private final int value;
    private final int remaining;
    private final TimeUnit unit;
    private final long resetTime;

    RateLimit(HTTPVerb verb, String URI, String regex, int value, int remaining, TimeUnit unit, long resetTime) {
        this.verb = verb;
        this.URI = URI;
        this.regex = regex;
        this.value = value;
        this.remaining = remaining;
        this.unit = unit;
        this.resetTime = resetTime;
    }

    public HTTPVerb getVerb() {
        return verb;
    }

    public String getURI() {
        return URI;
    }

    public String getRegex() {
        return regex;
    }

    public int getValue() {
        return value;
    }

    public int getRemaining() {
        return remaining;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public long getResetTime() {
        return resetTime;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("RateLimit");
        sb.append("{verb=").append(verb);
        sb.append(", URI='").append(URI).append('\'');
        sb.append(", regex='").append(regex).append('\'');
        sb.append(", value=").append(value);
        sb.append(", remaining=").append(remaining);
        sb.append(", unit=").append(unit);
        sb.append(", resetTime=").append(resetTime);
        sb.append('}');
        return sb.toString();
    }
}

class AbsoluteLimit implements Serializable {
    private final String name;
    private final int value;

    public AbsoluteLimit(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("AbsoluteLimit");
        sb.append("{name='").append(name).append('\'');
        sb.append(", value=").append(value);
        sb.append('}');
        return sb.toString();
    }
}
