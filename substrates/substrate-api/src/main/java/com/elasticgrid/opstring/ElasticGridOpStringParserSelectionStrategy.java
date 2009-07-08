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

package com.elasticgrid.opstring;

import org.rioproject.opstring.OpStringParser;
import org.rioproject.opstring.OpStringParserSelectionStrategy;
import org.rioproject.opstring.XmlOpStringParser;
import java.io.File;
import java.net.URL;

/**
 * {@link OpStringParserSelectionStrategy} selecting either the XML or Groovy parser.
 */
public class ElasticGridOpStringParserSelectionStrategy implements OpStringParserSelectionStrategy {
    public OpStringParser findParser(Object source) {
        OpStringParser parser;
        String filename;

        // handle either local filename or URL as source
        if (source instanceof URL || source instanceof File) {
            filename = source.toString();
        } else {
            throw new UnsupportedOperationException("There is no support for "
                                                    + source.getClass().getName() + " source");
        }

        // determine if this is the XML format or the Groovy one
        if (filename.endsWith(".xml"))
            parser = new XmlOpStringParser();
        else if (filename.endsWith(".groovy"))
            parser = new ElasticGridDSLOpStringParser();
        else
            throw new UnsupportedOperationException("There is no support for " + filename +  " format");

        return parser;
    }
    
}
