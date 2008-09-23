package com.elasticgrid.tools.ui

import groovy.swing.SwingBuilder
import java.awt.FlowLayout
import javax.swing.*

class MainPanel {

    def MainPanel() {
        builder = new SwingBuilder();
        frame = builder.frame(
                title: 'Elastic Grid Administration',
                size: [320, 200],
                layout: new FlowLayout(),
                defaultCloseOperation: WindowConstants.EXIT_ON_CLOSE) {
            label = label(text: 'Provisioners')
        }
        frame.show()
    }

}