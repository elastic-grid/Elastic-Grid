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

package com.elasticgrid.opstring

import org.rioproject.opstring.GroovyDSLOpStringParser
import groovy.xml.MarkupBuilder
import com.elasticgrid.substrates.Substrate
import com.elasticgrid.substrates.SubstratesRepository

class ElasticGridDSLOpStringParser extends GroovyDSLOpStringParser {
  protected void processAdditionalTags(MarkupBuilder builder, ExpandoMetaClass emc) {
    // add Elastic Grid specific contributions to the DSL
//    emc.clusters = { String cluster ->
//      builder.Groups { Group(cluster) }
//    }
//    emc.clusters = { String... clusters ->
//      builder.Groups {
//        clusters.each { Group(it) }
//      }
//    }
    // retreive all registered substrates
    def substrates = SubstratesRepository.findSubstrates()
    // register each substrate in the DSL
    substrates.each() {Substrate substrate ->
      logger.info "Adding DSL features from ${substrate.name} substrate..."
      substrate.addDomainSpecificLanguageFeatures builder, emc
    }
  }
}