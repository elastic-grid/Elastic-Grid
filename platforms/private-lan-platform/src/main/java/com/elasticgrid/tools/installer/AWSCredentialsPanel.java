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

import com.izforge.izpack.gui.TwoColumnConstraints;
import com.izforge.izpack.gui.TwoColumnLayout;
import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.xerox.amazonws.ec2.EC2Exception;
import com.xerox.amazonws.ec2.Jec2;
import javax.swing.*;
import org.jets3t.service.S3Service;
import org.jets3t.service.security.AWSCredentials;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;

/**
 * Custom IzPanel panel asking for EC2 credentials.
 * @author Jerome Bernard
 */
public class AWSCredentialsPanel extends AbstractInstallerPanel {
    private JTextField tfAwsAccessId;
    private JTextField tfAwsSecretKey;

    public AWSCredentialsPanel(InstallerFrame parent, InstallData installData) {
        super(parent, installData);

        // setup layout manager
        TwoColumnLayout layout = new TwoColumnLayout(10, 5, 30, 25, TwoColumnLayout.LEFT);
        setLayout(layout);

        // setup title
        title("Integration with Amazon EC2")
                .text("Elastic Grid needs your AWS credentials so that it can start some EC2 servers for you based on your requirements and so that it can upload your applications to Amazon S3 so that they can be easily deployed.")
                .space();

        // setup AWS access ID field
        tfAwsAccessId = textField("AWS Access ID");
        tfAwsAccessId.requestFocusInWindow();
        tfAwsSecretKey = textField("AWS Secret Key");

        space().space().text("You can find your AWS credentials on the Amazon Web Site Access Identifiers page.");

        getLayoutHelper().completeLayout();
    }

    @Override
    protected boolean isValidated() {
        String awsAccessId = tfAwsAccessId.getText();
        String awsSecretKey = tfAwsSecretKey.getText();
        try {
            Jec2 ec2 = new Jec2(awsAccessId, awsSecretKey);
            S3Service s3 = new RestS3Service(new AWSCredentials(awsAccessId, awsSecretKey));
            ec2.describeKeyPairs(new String[] {});
            idata.setAttribute("ec2", ec2);
            idata.setAttribute("s3", s3);
            idata.setVariable(Constants.AWS_ACCESS_ID, awsAccessId);
            idata.setVariable(Constants.AWS_SECRET_KEY, awsSecretKey);
            return true;
        } catch (IllegalArgumentException e) {
            if ("".equals(tfAwsAccessId.getText()))
                JOptionPane.showMessageDialog(parent, "The AWS Access ID has to be filled in!", "Invalid EC2 credentials", JOptionPane.ERROR_MESSAGE);
            else if ("".equals(tfAwsSecretKey.getText()))
                JOptionPane.showMessageDialog(parent, "The AWS Secret Key has to be filled in!", "Invalid EC2 credentials", JOptionPane.ERROR_MESSAGE);
            else
                JOptionPane.showMessageDialog(parent, e.getMessage(), "Invalid EC2 credentials", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (NullPointerException e) {
            int result = JOptionPane.showConfirmDialog(parent,
                    "<html>It appears that the internet connection is not working propertly, so your EC2 credentials " +
                            "can't be verified.<br>Should the installer switch to offline mode instead and skip the " +
                            "verification process?</html>", "Offline mode?", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.NO_OPTION) {
                return false;
            } else {
                idata.setVariable(Constants.OFFLINE_MODE, "true");
                return true;
            }
        } catch (EC2Exception e) {
            JOptionPane.showMessageDialog(parent, e.getMessage(), "Invalid EC2 credentials", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, e.toString(), "Invalid EC2 credentials", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
