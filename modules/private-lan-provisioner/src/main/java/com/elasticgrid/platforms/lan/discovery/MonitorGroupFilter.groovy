/**
 * Elastic Grid
 * Copyright (C) 2008-2010 Elastic Grid, LLC.
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
package com.elasticgrid.platforms.lan.discovery

import net.jini.core.lookup.ServiceItem
import net.jini.lookup.ServiceItemFilter

public class MonitorGroupFilter implements ServiceItemFilter {
  def String clusterName

  def MonitorGroupFilter(clusterName) {
    this.clusterName = clusterName;
  }

  boolean check(ServiceItem item) {
//    def ProvisionMonitor monitor = item.service.admin
//    def ServiceElement serviceElement = cybernode.serviceElement
//    def ServiceBeanConfig config = serviceElement.serviceBeanConfig
//    return config.groups.find { it == clusterName } != null
    return true
  }
}