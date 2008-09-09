/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This file is part of Elastic Grid.
 * 
 * Elastic Grid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * 
 * Elastic Grid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Elastic Grid.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elasticgrid.opstring

import org.rioproject.opstring.GroovyDSLOpStringParser
import groovy.xml.MarkupBuilder
import com.elasticgrid.substrates.Substrate
import com.elasticgrid.substrates.SubstratesRepository

class ElasticGridDSLOpStringParser extends GroovyDSLOpStringParser {
    protected void processAdditionalTags(MarkupBuilder builder, ExpandoMetaClass emc) {
        // retreive all registered substrates
        def substrates = SubstratesRepository.findSubstrates()
        // register each substrate in the DSL
        substrates.each() { Substrate substrate ->
            logger.info "Adding DSL features from ${substrate.name} substrate..."
            substrate.addDomainSpecificLangueFeatures builder, emc
        }
    }
}