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
package com.elasticgrid.tools.installer;

import com.izforge.izpack.gui.LabelFactory;
import com.izforge.izpack.gui.TwoColumnConstraints;
import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.izforge.izpack.installer.IzPanel;
import java.util.List;
import javax.swing.*;
import java.awt.*;

/**
 * Abstract IzPack panel helping creation of input panels.
 * @author Jerome Bernard
 */
public abstract class AbstractInstallerPanel extends IzPanel {
    public AbstractInstallerPanel(InstallerFrame parent, InstallData installData) {
        super(parent, installData);
    }

    protected AbstractInstallerPanel title(String title) {
//        JLabel label = LabelFactory.create(title);
        JLabel label = LabelFactory.create(title, null, JLabel.TRAILING, true);
        Font font = label.getFont();
        font = font.deriveFont(0, font.getSize() * 2.0f);
        label.setFont(font);
        label.setAlignmentX(0);

        TwoColumnConstraints constraints = new TwoColumnConstraints();
        constraints.align = TwoColumnConstraints.LEFT;
        constraints.position = TwoColumnConstraints.NORTH;

        add(label, constraints);
        return this;
    }

    protected AbstractInstallerPanel text(String message) {
        JTextPane label = new JTextPane();
        label.setEditable(false);
        label.setText(message);
        label.setBackground(UIManager.getColor("label.backgroud"));
        label.setMargin(new Insets(3, 0, 3, 0));
        label.getPreferredSize();

        TwoColumnConstraints constraints = new TwoColumnConstraints();
        constraints.position = TwoColumnConstraints.BOTH;
        constraints.stretch = true;

        add(label, constraints);
        return this;
    }

    protected JTextField textField(String message, int... size) {
        JLabel label = new JLabel(message);
        TwoColumnConstraints constraints = new TwoColumnConstraints();
        constraints.position = TwoColumnConstraints.WEST;
        add(label, constraints);

        JTextField textField;
        if (size.length == 0)
            textField = new JTextField();
        else
            textField = new JTextField(size[0]);
        constraints.stretch = true;
        constraints.position = TwoColumnConstraints.EAST;
        add(textField, constraints);

        return textField;
    }

    protected JComboBox combo(String message, List<String> options, boolean editable) {
        JLabel label = new JLabel(message);
        TwoColumnConstraints constraints = new TwoColumnConstraints();
        constraints.position = TwoColumnConstraints.WEST;
        add(label, constraints);

        JComboBox comboBox = new JComboBox();
        comboBox.setModel(new DefaultComboBoxModel(options.toArray(new Object[options.size()])));
        comboBox.setEditable(editable);
        constraints.stretch = true;
        constraints.position = TwoColumnConstraints.EAST;
        add(comboBox, constraints);

        return comboBox;
    }

    protected JList list(String message, List<String> options) {
        JLabel label = new JLabel(message);
        TwoColumnConstraints constraints = new TwoColumnConstraints();
        constraints.position = TwoColumnConstraints.WEST;
        add(label, constraints);

        JList list = new JList();
        list.setModel(new DefaultComboBoxModel(options.toArray(new Object[options.size()])));
        list.setEnabled(false);
        constraints.stretch = true;
        constraints.position = TwoColumnConstraints.EAST;
        add(list, constraints);

        return list;
    }

    protected AbstractInstallerPanel space() {
        TwoColumnConstraints constraints = new TwoColumnConstraints();
        constraints.position = TwoColumnConstraints.BOTH;
        constraints.stretch = true;
        add(new JPanel(), constraints);
        return this;
    }
}