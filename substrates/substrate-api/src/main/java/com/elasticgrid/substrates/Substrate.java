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
package com.elasticgrid.substrates;

import org.rioproject.watch.Watch;

import java.util.Set;
import java.util.List;

import groovy.lang.ExpandoMetaClass;
import groovy.xml.MarkupBuilder;

public interface Substrate {

    /**
     * Return the name of the underlying application the substrate acts for.
     * @return the name of the underlying applicaiton
     */
    String getName();

    /**
     * Enable the substrate to enrich the DSL.
     * @param builder the XML {@link MarkupBuilder}
     * @param emc the {@link ExpandoMetaClass} to use in order to inject custom parsing logic
     */
    void addDomainSpecificLanguageFeatures(MarkupBuilder builder, ExpandoMetaClass emc);

    /**
     * Return the list of {@link Watch}es for the underlying application.
     * @return the list of watches
     */
    Set<Watch> getWatches();

    /**
     * Return the list of {@link FirewallRule}s to activate for the substrate.
     * @return the list of firewall rules
     */
    List<FirewallRule> getFirewallRules();

}
