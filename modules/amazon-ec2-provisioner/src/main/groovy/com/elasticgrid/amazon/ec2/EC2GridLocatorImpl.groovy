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

package com.elasticgrid.amazon.ec2

import com.xerox.amazonws.ec2.Jec2
import java.util.logging.Logger
import com.elasticgrid.model.ec2.EC2Node
import com.elasticgrid.model.GridNotFoundException
import com.elasticgrid.model.GridException
import com.xerox.amazonws.ec2.ReservationDescription
import com.xerox.amazonws.ec2.EC2Exception
import java.util.logging.Level
import com.elasticgrid.model.ec2.impl.EC2NodeImpl
import com.elasticgrid.model.NodeProfile

class EC2GridLocatorImpl implements EC2GridLocator {
    def Jec2 ec2
    public static final String EG_GROUP_MONITOR = "eg-monitor"
    public static final String EG_GROUP_AGENT = "eg-agent"
    private static final Logger logger = Logger.getLogger(EC2GridLocatorImpl.class.name)

    public List<EC2Node> findNodes(String gridName) throws GridNotFoundException, GridException {
        // retrieve the list of instances running
        logger.log Level.INFO, "Searching for Elastic Grid nodes in grid '{0}'...", gridName
        List<ReservationDescription> reservations
        try {
            reservations = ec2.describeInstances(Collections.<String>emptyList())
        } catch (EC2Exception e) {
            throw new GridException("Can't locate grid $gridName", e)
        }
        // filter nodes which are not part of the grid
        def gridReservation = reservations.findAll { it.groups.contains gridName }
        // build of list of all the instances
        return gridReservation.collect { ReservationDescription reservation ->
            reservation.instances.collect { ReservationDescription.Instance instance ->
                boolean monitor = reservation.groups.contains(NodeProfile.MONITOR.toString())
                boolean agent = reservation.groups.contains(NodeProfile.AGENT.toString())
                def profile = null;
                if (!agent && !monitor) {
                    logger.log Level.WARNING, "Instance ${instance.instanceID} has no Elastic Grid profile!"
                    return
                } else if (agent && monitor) {
                    logger.log Level.WARNING,
                               "Instance ${instance.instanceID} is both a monitor and an agent. Using it as a monitor!"
                    profile = NodeProfile.MONITOR
                } else if (monitor) {
                    profile = NodeProfile.MONITOR
                } else if (agent) {
                    profile = NodeProfile.AGENT
                }
                return new EC2NodeImpl(
                        'instanceID': instance.instanceId,
                        'profile': profile,
                        'address': instance.dnsName as InetAddress
                )
            }
        }.flatten()
    }

    public void setEc2(Jec2 ec2) {
        this.ec2 = ec2;
    }
}