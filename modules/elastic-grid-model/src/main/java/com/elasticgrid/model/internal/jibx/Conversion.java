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

package com.elasticgrid.model.internal.jibx;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Currency;
import com.elasticgrid.model.NodeProfile;

/**
 * Custom XML serializers/deserializers for JiBX.
 * @author Jerome Bernard
 */
public class Conversion {
    private static final DateTimeFormatter DATE_FORMATTER = ISODateTimeFormat.dateTime();
    private static final DateTimeFormatter DATE_FORMATTER_WITHOUT_MILLIS = ISODateTimeFormat.dateTimeNoMillis();
    private static final PeriodFormatter DURATION_FORMATTER = ISOPeriodFormat.standard();

    public static String serializeCurrency(Currency currency) {
        return currency.getCurrencyCode();
    }

    public static Currency deserializeCurrency(String currencyCode) {
        return Currency.getInstance(currencyCode);
    }

    public static String serializeDateTime(DateTime when) {
        if (when == null)
            return null;
        return DATE_FORMATTER.print(when);
    }

    public static DateTime deserializeDateTime(String when) {
        if (StringUtils.isEmpty(when))
            return null;
        return DATE_FORMATTER.parseDateTime(when);
    }

    public static String serializeDateTimeWithoutMillis(DateTime when) {
        if (when == null)
            return null;
        return DATE_FORMATTER_WITHOUT_MILLIS.print(when);
    }

    public static DateTime deserializeDateTimeWithoutMillis(String when) {
        if (StringUtils.isEmpty(when))
            return null;
        return DATE_FORMATTER_WITHOUT_MILLIS.parseDateTime(when);
    }

    public static String serializePeriod(Period period) {
        if (period == null)
            return null;
        return DURATION_FORMATTER.print(period);
    }

    public static Period deserializePeriod(String period) {
        if (StringUtils.isEmpty(period))
            return null;
        return DURATION_FORMATTER.parsePeriod(period);
    }

    public static String serializeInetAddress(InetAddress address) {
        if (address == null)
            return null;
        return address.getCanonicalHostName();
    }

    public static InetAddress deserializeInetAddress(String address) throws UnknownHostException {
        if (StringUtils.isEmpty(address))
            return null;
        return InetAddress.getByName(address);
    }

    public static String serializeNodeProfile(NodeProfile profile) {
        if (profile == null)
            return null;
        switch (profile) {
            case MONITOR:
                return "monitor";
            case AGENT:
                return "agent";
        }
        throw new IllegalArgumentException("Unexpected profile " + profile);
    }

    public static NodeProfile deserializeNodeProfile(String profile) throws UnknownHostException {
        if (StringUtils.isEmpty(profile))
            return null;
        if ("monitor".equals(profile))
            return NodeProfile.MONITOR;
        else if ("agent".equals(profile))
            return NodeProfile.AGENT;
        throw new IllegalArgumentException("Unexpected profile " + profile);
    }

}
