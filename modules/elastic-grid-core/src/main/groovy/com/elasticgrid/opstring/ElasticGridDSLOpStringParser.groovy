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

package com.elasticgrid.opstring

import org.rioproject.opstring.GroovyDSLOpStringParser
import groovy.xml.MarkupBuilder
import com.elasticgrid.substrates.Substrate

class ElasticGridDSLOpStringParser extends GroovyDSLOpStringParser {
    def SUBSTRATE_DSL_LOCATION = "META-INF/com/elasticgrid/substrates/SubstrateClass"

    protected void processAdditionalTags(MarkupBuilder builder, ExpandoMetaClass emc) {
        URLClassLoader cl = Thread.currentThread().contextClassLoader
        List<Substrate> substrates = []
        // search for all JARs having some substrates declared
        def resources = cl.getResources(SUBSTRATE_DSL_LOCATION)
        // extract substrate class names and build them
        resources.each { URL url ->
            def substrateClassName = url.content.text
            def substrate = cl.loadClass(substrateClassName).newInstance()
            substrates << substrate
        }
        // register each substrate in the DSL
        substrates.each() { Substrate substrate ->
            logger.info "Adding DSL features from ${substrate.name} substrate..."
            substrate.addDomainSpecificLangueFeatures builder, emc
        }
    }
}