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

import org.rioproject.watch.Calculable;
import org.rioproject.watch.PeriodicWatch;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * MySQL specific watches.
 *
 */
public class MySQLStatusWatch extends PeriodicWatch {
    private String variableName;
    private Class variableClass;
    private PreparedStatement statement;

    public MySQLStatusWatch(String variableName, Class variableClass, PreparedStatement statement) {
        super(String.format("MySQL status for %s", variableName));
        this.variableName = variableName;
        this.variableClass = variableClass;
        this.statement = statement;
    }

    public void checkValue() {
        try {
            statement.setString(0, variableName);
            ResultSet results = statement.executeQuery();
            long value = results.getLong("Value");
            super.addWatchRecord(new Calculable(getId(), value));
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Could not log watch value", e);
        }
    }
}
