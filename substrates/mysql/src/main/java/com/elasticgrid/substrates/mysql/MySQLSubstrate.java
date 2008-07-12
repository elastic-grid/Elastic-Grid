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

package com.elasticgrid.substrates.mysql;

import com.elasticgrid.substrates.AbstractSubstrate;
import com.elasticgrid.substrates.Substrate;
import net.jini.config.Configuration;

import java.sql.*;
import java.util.logging.Level;

public class MySQLSubstrate extends AbstractSubstrate implements Substrate {
    private PreparedStatement statusStatement;
    private Connection connection;
    private static final String SUBSTRATE_COMPONENT = "com.elasticgrid.substrates.mysql";

    protected void initialize(Configuration config) throws Exception {
        String jdbcUrl = (String) config.getEntry(SUBSTRATE_COMPONENT, "jdbcUrl", String.class);
        String username = (String) config.getEntry(SUBSTRATE_COMPONENT, "username", String.class);
        String password = (String) config.getEntry(SUBSTRATE_COMPONENT, "password", String.class);
        Class.forName(com.mysql.jdbc.Driver.class.getName());
        connection = DriverManager.getConnection(jdbcUrl, username, password);
        statusStatement = connection.prepareStatement("show status like '?'");
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet variables = statement.executeQuery("select status");
            while (variables.next()) {
                String variableName = variables.getString(0);
                Object o = variables.getObject(1);
                registerWatch(new MySQLStatusWatch(variableName, o.getClass(), statusStatement));
            }
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Could not properly close MySQL statement", e);
                }
            }
        }
    }

    public void destroy() {
        try {
            statusStatement.close();
            connection.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Could not close MySQL connection", e);
        }
    }

    public String getName() {
        return "MySQL";
    }

}
