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

package com.elasticgrid.utils.amazon;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;

public class Utils {

    public static Properties loadEC2Configuration() throws IOException {
        Properties ec2 = new Properties();
        InputStream stream = null;
        try {
            File awsConfigurationFile = new File(System.getProperty("user.home") + File.separator + ".eg",
                    "aws.properties");
            stream = new FileInputStream(awsConfigurationFile);
            ec2.load(stream);
        } finally {
            try {
                if (stream != null)
                    stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ec2;
    }

}
