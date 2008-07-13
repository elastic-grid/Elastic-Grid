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

package com.elasticgrid.opstring;

import org.rioproject.opstring.GroovyDSLOpStringParser;
import org.rioproject.opstring.OpStringParser;
import org.rioproject.opstring.OpStringParserSelectionStrategy;
import org.rioproject.opstring.XmlOpStringParser;
import java.io.File;
import java.net.URL;

/**
 * {@link OpStringParserSelectionStrategy}Êselecting either the XML or Groovy parser.
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
