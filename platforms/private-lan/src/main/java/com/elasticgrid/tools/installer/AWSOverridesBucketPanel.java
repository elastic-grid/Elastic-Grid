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
import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.KeyPairInfo;
import com.xerox.amazonws.ec2.EC2Exception;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Bucket;

/**
 * Custom IzPanel panel asking for S3 overrides bucket.
 * @author Jerome Bernard
 */
public class AWSOverridesBucketPanel extends AbstractInstallerPanel {
    private JComboBox cbBuckets;
    private List<String> buckets = new LinkedList<String>();

    public AWSOverridesBucketPanel(InstallerFrame parent, InstallData installData) {
        super(parent, installData);

        // setup layout manager
        TwoColumnLayout layout = new TwoColumnLayout(10, 5, 30, 25, TwoColumnLayout.LEFT);
        setLayout(layout);

        // setup title
        title("Integration with Amazon EC2")
                .text("Elastic Grid needs to know the name of the S3 bucket where your Elastic Grid overrides will be uploaded.")
                .space();

        // setup AWS buckets field
        cbBuckets = combo("Overrides Buckets", buckets, true);
        cbBuckets.requestFocusInWindow();

        getLayoutHelper().completeLayout();
    }

    @Override
    public void panelActivate() {
        try {
            buckets.clear();
            S3Service s3 = (S3Service) idata.getAttribute("s3");
            S3Bucket[] s3Buckets = s3.listAllBuckets();
            for (S3Bucket bucket : s3Buckets)
                buckets.add(bucket.getName());
            cbBuckets.setModel(new DefaultComboBoxModel(buckets.toArray(new Object[buckets.size()])));
        } catch (S3ServiceException e) {
            JOptionPane.showMessageDialog(parent, e.getMessage(), "Amazon S3 error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    protected boolean isValidated() {
        Object o = cbBuckets.getSelectedItem();
        if (o == null || "".equals(o.toString())) {
            JOptionPane.showMessageDialog(parent, "The override bucket must be filled in!",
                    "Invalid Override Bucket", JOptionPane.ERROR_MESSAGE);
            return false;
        } else {
            String overridesBucket = o.toString();
            S3Service s3 = (S3Service) idata.getAttribute("s3");
            try {
                S3Bucket[] s3Buckets = s3.listAllBuckets();
                List<String> b = new ArrayList<String>(s3Buckets.length);
                for (S3Bucket bucket : s3Buckets)
                    b.add(bucket.getName());
                if (b.contains(overridesBucket)) {
                    idata.setVariable(Constants.EG_OVERRIDES_BUCKET, overridesBucket);
                } else {
                    int result = JOptionPane.showConfirmDialog(parent, "Do you want to create this S3 Overrides Bucket?",
                            "S3 Overrides Bucket Creation", JOptionPane.YES_NO_OPTION);
                    if (JOptionPane.YES_OPTION == result) {
                        try {
                            s3.createBucket(overridesBucket);
                        } catch (S3ServiceException e) {
                            JOptionPane.showMessageDialog(parent, e.getMessage(),
                                    "Can't create S3 Overrides Bucket", JOptionPane.ERROR_MESSAGE);
                        }
                        return isValidated();
                    } else {
                        return false;
                    }
                }
                return true;
            } catch (S3ServiceException e) {
                JOptionPane.showMessageDialog(parent, e.getMessage(), "Amazon S3 error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
    }

}