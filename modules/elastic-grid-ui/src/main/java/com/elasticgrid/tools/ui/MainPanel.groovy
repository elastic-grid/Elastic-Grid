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

package com.elasticgrid.tools.ui

import com.elasticgrid.grid.discovery.GridLocator
import com.elasticgrid.grid.GridManager
import com.elasticgrid.model.GridMonitorNotFoundException
import com.elasticgrid.model.NodeProfile
import com.elasticgrid.model.ec2.EC2Grid
import com.elasticgrid.model.ec2.impl.EC2GridImpl
import com.elasticgrid.model.ec2.impl.EC2NodeImpl
import groovy.swing.SwingBuilder
import javax.swing.JOptionPane
import javax.swing.ListSelectionModel
import javax.swing.WindowConstants
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener
import org.rioproject.resources.util.XMLAndGroovyFileChooser

class MainPanel {

    def MainPanel(GridManager gridManager, GridLocator locator) {
        def builder = new SwingBuilder();

        // Test Data: comment the few lines below and uncomment the one using the Grid Manager
        def EC2Grid grid1 = new EC2GridImpl(
                name: 'test',
                nodes: [
                    new EC2NodeImpl(instanceID: 'instanceID', profile: NodeProfile.MONITOR, address: InetAddress.localHost),
                    new EC2NodeImpl(instanceID: 'instanceID', profile: NodeProfile.AGENT, address: InetAddress.localHost)
                ] as Set
        )
        def EC2Grid grid2 = new EC2GridImpl(
                name: 'another',
                nodes: [
                    new EC2NodeImpl(instanceID: 'instanceID', profile: NodeProfile.MONITOR, address: InetAddress.localHost),
                    new EC2NodeImpl(instanceID: 'instanceID', profile: NodeProfile.AGENT, address: InetAddress.localHost),
                    new EC2NodeImpl(instanceID: 'instanceID', profile: NodeProfile.AGENT, address: InetAddress.localHost)
                ] as Set
        )
        def grids = [ grid1, grid2 ]

        //def grids = gridManager.grids
        def GridsTableModel gridsModel = new GridsTableModel(grids: grids)
        def NodesTableModel nodesModel = new NodesTableModel()

        def frame = builder.frame(
                title: 'Elastic Grid Administration',
                defaultCloseOperation: WindowConstants.EXIT_ON_CLOSE) { frame ->
            borderLayout()
            panel(constraints: NORTH) {
                borderLayout()
                label = label(text: 'Grids', constraints: NORTH)
                scrollPane(constraints: CENTER) {
                    table(id: 'gridsTable') {
                        tableModel(gridsModel)
                        current.selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
                        current.selectionModel.addListSelectionListener({ ListSelectionEvent e ->
                            def row = e.source.leadIndex
                            if (!e.valueIsAdjusting)
                                nodesModel.nodes = grids[row].nodes
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
                        def selectedGrid = grids[gridsTable.selectedRow]
                        try {
                            def monitor = locator.findMonitor(selectedGrid.name)
                            println "Should deploy OpString '$opstring' to grid '${selectedGrid.name}' whose monitor is ${monitor}"
                        } catch (GridMonitorNotFoundException e) {
                            showError "<html>Monitor not found for grid '${selectedGrid.name}'.<br>No deployment can be made!</html>", frame
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