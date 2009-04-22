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
package com.elasticgrid.tools.installer;

import com.izforge.izpack.gui.TwoColumnLayout;
import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.xerox.amazonws.ec2.EC2Exception;
import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.KeyPairInfo;
import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Custom IzPanel panel asking for EC2 keypair.
 * @author Jerome Bernard
 */
public class AWSKeyPairPanel extends AbstractInstallerPanel {
    private JComboBox cbKeyPairs;
    private List<String> keypairs = new LinkedList<String>();

    public AWSKeyPairPanel(InstallerFrame parent, InstallData installData) {
        super(parent, installData);

        // setup layout manager
        TwoColumnLayout layout = new TwoColumnLayout(10, 5, 30, 25, TwoColumnLayout.LEFT);
        setLayout(layout);

        // setup title
        title("Integration with Amazon EC2")
                .text("Elastic Grid needs a valid EC2 KeyPair so that you can log into the machines through SSH connections.")
                .space();

        // setup AWS keypairs field
        cbKeyPairs = combo("EC2 KeyPair", keypairs, true);
        cbKeyPairs.requestFocusInWindow();

        getLayoutHelper().completeLayout();
    }

    @Override
    public void panelActivate() {
        Jec2 ec2 = (Jec2) idata.getAttribute("ec2");
        try {
            keypairs.clear();
            List<KeyPairInfo> pairs = ec2.describeKeyPairs(new String[] {});
            for (KeyPairInfo keypair : pairs)
                keypairs.add(keypair.getKeyName());
            cbKeyPairs.setModel(new DefaultComboBoxModel(keypairs.toArray(new Object[keypairs.size()])));
        } catch (EC2Exception e) {
            JOptionPane.showMessageDialog(parent, e.getMessage(),
                    "Can't get list of EC2 KeyPairs", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    protected boolean isValidated() {
        Object o = cbKeyPairs.getSelectedItem();
        if (o == null || "".equals(o.toString())) {
            JOptionPane.showMessageDialog(parent, "The keypair must be filled in!",
                    "Invalid EC2 keypair", JOptionPane.ERROR_MESSAGE);
            return false;
        } else {
            String keypair = o.toString();
            Jec2 ec2 = (Jec2) idata.getAttribute("ec2");
            try {
                ec2.describeKeyPairs(new String[]{keypair});
                idata.setVariable(Constants.AWS_KEYPAIR, keypair);
                return true;
            } catch (EC2Exception e) {
                int result = JOptionPane.showConfirmDialog(parent, "Do you want to create this EC2 KeyPair?",
                        "EC2 KeyPair Creation", JOptionPane.YES_NO_OPTION);
                if (JOptionPane.YES_OPTION == result) {
                    try {
                        ec2.createKeyPair(keypair);
                    } catch (EC2Exception e1) {
                        JOptionPane.showMessageDialog(parent, e.getMessage(),
                                "Can't create EC2 KeyPair", JOptionPane.ERROR_MESSAGE);
                    }
                    return isValidated();
                } else {
                    return false;
                }
            }
        }
    }

}