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

package com.elasticgrid.tools.ui

import com.elasticgrid.cluster.discovery.ClusterLocator
import com.elasticgrid.cluster.ClusterManager
import com.elasticgrid.model.ClusterMonitorNotFoundException
import com.elasticgrid.model.NodeProfile
import com.elasticgrid.model.ec2.EC2Cluster
import com.elasticgrid.model.ec2.impl.EC2ClusterImpl
import com.elasticgrid.model.ec2.impl.EC2NodeImpl
import groovy.swing.SwingBuilder
import javax.swing.JOptionPane
import javax.swing.ListSelectionModel
import javax.swing.WindowConstants
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener
import org.rioproject.resources.util.XMLAndGroovyFileChooser

class MainPanel {

    def MainPanel(ClusterManager clusterManager, ClusterLocator locator) {
        def builder = new SwingBuilder();

        // Test Data: comment the few lines below and uncomment the one using the Cluster Manager
        def EC2Cluster cluster1 = new EC2ClusterImpl(
                name: 'test',
                nodes: [
                    new EC2NodeImpl(instanceID: 'instanceID', profile: NodeProfile.MONITOR_AND_AGENT, address: InetAddress.localHost),
                    new EC2NodeImpl(instanceID: 'instanceID', profile: NodeProfile.AGENT, address: InetAddress.localHost)
                ] as Set
        )
        def EC2Cluster cluster2 = new EC2ClusterImpl(
                name: 'another',
                nodes: [
                    new EC2NodeImpl(instanceID: 'instanceID', profile: NodeProfile.MONITOR_AND_AGENT, address: InetAddress.localHost),
                    new EC2NodeImpl(instanceID: 'instanceID', profile: NodeProfile.AGENT, address: InetAddress.localHost),
                    new EC2NodeImpl(instanceID: 'instanceID', profile: NodeProfile.AGENT, address: InetAddress.localHost)
                ] as Set
        )
        def clusters = [ cluster1, cluster2 ]

        //def clusters = clusterManager.clusters
        def ClustersTableModel clustersModel = new ClustersTableModel(clusters: clusters)
        def NodesTableModel nodesModel = new NodesTableModel()

        def frame = builder.frame(
                title: 'Elastic Grid Administration',
                defaultCloseOperation: WindowConstants.EXIT_ON_CLOSE) { frame ->
            borderLayout()
            panel(constraints: NORTH) {
                borderLayout()
                label = label(text: 'Clusters', constraints: NORTH)
                scrollPane(constraints: CENTER) {
                    table(id: 'clustersTable') {
                        tableModel(clustersModel)
                        current.selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
                        current.selectionModel.addListSelectionListener({ ListSelectionEvent e ->
                            def row = e.source.leadIndex
                            if (!e.valueIsAdjusting)
                                nodesModel.nodes = clusters[row].nodes
                        } as ListSelectionListener)
                    }
                }
            }

            panel(constraints: SOUTH) {
                borderLayout()
                label = label(text: 'Nodes', constraints: NORTH)
                scrollPane(constraints: CENTER) {
                    table(id: 'nodes') {
                        tableModel(nodesModel)
                    }
                }
                panel(constraints: SOUTH) {
                    button('Deploy...', actionPerformed: {
                        XMLAndGroovyFileChooser fileChooser = new XMLAndGroovyFileChooser(frame, null, "Choose OpString to deploy")
                        def opstring = fileChooser.file
                        def selectedCluster = clusters[clustersTable.selectedRow]
                        try {
                            def monitor = locator.findMonitor(selectedCluster.name)
                            println "Should deploy OpString '$opstring' to cluster '${selectedCluster.name}' whose monitor is ${monitor}"
                        } catch (ClusterMonitorNotFoundException e) {
                            showError "<html>Monitor not found for cluster '${selectedCluster.name}'.<br>No deployment can be made!</html>", frame
                        }
                    })
                    button('Undeploy...', actionPerformed: {
                        println "Should propose a list of deployed OpString and undeploy the selected one!"
                    })
                }
            }

        }        
        frame.pack()
        frame.show()
    }

    def showError(String message, window) {
        JOptionPane.showMessageDialog(window, message, "Error", JOptionPane.ERROR_MESSAGE)
    }

}
