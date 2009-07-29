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
package com.elasticgrid.rackspace;

import java.io.Serializable;

/**
 * Server backup schedule.
 *
 * @author Jerome Bernard
 */
public class BackupSchedule implements Serializable {
    private boolean enabled;
    private WeeklyBackup weekly;
    private DailyBackup daily;

    public BackupSchedule(boolean enabled, WeeklyBackup weekly, DailyBackup daily) {
        this.enabled = enabled;
        this.weekly = weekly;
        this.daily = daily;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public WeeklyBackup getWeekly() {
        return weekly;
    }

    public DailyBackup getDaily() {
        return daily;
    }

    public enum WeeklyBackup implements Serializable {
        DISABLED("DISABLED"), SUNDAY("SUNDAY"), MONDAY("MONDAY"), TUESDAY("TUESDAY"),
        WEDNESDAY("WEDNESDAY"), THURSDAY("THURSDAY"), FRIDAY("FRIDAY"),
        SATURDAY("SATURDAY"), SUNDAY1("SUNDAY");
        private final String value;

        private WeeklyBackup(String value) {
            this.value = value;
        }
    }

    public enum DailyBackup implements Serializable {
        DISABLED, H_0000_0200, H_0200_0400, H_0400_0600, H_0600_0800, H_0800_1000,
        H_1000_1200, H_1200_1400, H_1400_1600, H_1600_1800, H_1800_2000,
        H_2000_2200, H_2200_0000
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("BackupSchedule");
        sb.append("{enabled=").append(enabled);
        sb.append(", weekly=").append(weekly);
        sb.append(", daily=").append(daily);
        sb.append('}');
        return sb.toString();
    }
}
