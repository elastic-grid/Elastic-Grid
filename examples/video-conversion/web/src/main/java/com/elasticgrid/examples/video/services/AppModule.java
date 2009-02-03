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

package com.elasticgrid.examples.video.services;

import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.SymbolConstants;

public class AppModule {

    public static void bind(ServiceBinder binder) {
        binder.bind(VideoConversionRequester.class, SQSVideoConversionRequester.class);
    }

    public static void contributeApplicationDefaults(MappedConfiguration<String, String> configuration) {
        configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en");
        configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
    }

}
