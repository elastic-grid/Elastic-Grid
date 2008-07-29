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

package com.elasticgrid.utils.jibx;

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

}
