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
import com.xerox.amazonws.ec2.GroupDescription;
import javax.swing.*;
import java.util.Arrays;
import java.util.List;

/**
 * Custom IzPanel panel setting up EC2 security groups.
 * @author Jerome Bernard
 */
public class AWSSecurityGroupsPanel extends AbstractInstallerPanel {
    private JList lSecurityGroups;
    private List<String> groups = Arrays.asList("eg-monitor-and-agent", "eg-agent", "elastic-grid");

    public AWSSecurityGroupsPanel(InstallerFrame parent, InstallData installData) {
        super(parent, installData);

        // setup layout manager
        TwoColumnLayout layout = new TwoColumnLayout(10, 5, 30, 25, TwoColumnLayout.LEFT);
        setLayout(layout);

        // setup title
        title("Integration with Amazon EC2")
                .text("Elastic Grid needs some valid security groups allowing your desktop to connect to the EC2 instances.")
                .space();

        // setup AWS groups field
        lSecurityGroups = list("EC2 Security Groups to be created", groups);

        space().space()
                .text("Elastic Grid Security Groups open required firewall ports so that your EC2 instances can be reached from SSH or Elastic Grid tools.");

        getLayoutHelper().completeLayout();
    }

    @Override
    protected boolean isValidated() {
        Jec2 ec2 = (Jec2) idata.getAttribute("ec2");

        try {
            // ensure "elastic-grid" group is available
            createSecurityGroupIfNeeded("elastic-grid", "Elastic Grid Security Group");
            // allow SSH to be reached from any IP
            authorizeIfNeeded("elastic-grid", "tcp", 22, "0.0.0.0/0");

            // ensure "eg-monitor" group is available
            createSecurityGroupIfNeeded("eg-monitor", "Elastic Grid Monitors Security Group");
            // authorize REST API to be reached
            authorizeIfNeeded("eg-monitor", "tcp", 8182, "0.0.0.0/0");

            // ensure "eg-agent" group is available
            createSecurityGroupIfNeeded("eg-agent", "Elastic Grid Agents Security Group");
            
            return true;
        } catch (EC2Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, e.getMessage(),
                                "Can't create EC2 Security Groups", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void createSecurityGroupIfNeeded(String groupName, String groupDescription) throws EC2Exception {
        Jec2 ec2 = (Jec2) idata.getAttribute("ec2");
        try {
            ec2.describeSecurityGroups(Arrays.asList(groupName));
        } catch (EC2Exception e) {
            ec2.createSecurityGroup(groupName, groupDescription);
        }
    }

    private void authorizeIfNeeded(String groupName, String protocol, int port, String cidr) throws EC2Exception {
        Jec2 ec2 = (Jec2) idata.getAttribute("ec2");
        GroupDescription group = ec2.describeSecurityGroups(Arrays.asList(groupName)).get(0);
        boolean open = false;
        for (GroupDescription.IpPermission perm : group.getPermissions()) {
            if (perm.getFromPort() <= port && port <= perm.getToPort() && perm.getProtocol().equals(protocol)) {
                // firewall is open for that port/protocol
                open = true;
            }
        }
        if (!open) {
            // setup the firewall rule
            ec2.authorizeSecurityGroupIngress(groupName, protocol, port, port, cidr);
        }
    }

}
