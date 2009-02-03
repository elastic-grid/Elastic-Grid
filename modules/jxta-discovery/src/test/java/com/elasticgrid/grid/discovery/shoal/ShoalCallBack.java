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

package com.elasticgrid.grid.discovery.shoal;

import com.sun.enterprise.ee.cms.core.CallBack;
import com.sun.enterprise.ee.cms.core.Signal;
import com.sun.enterprise.ee.cms.core.MessageSignal;
import com.sun.enterprise.ee.cms.core.SignalAcquireException;
import com.sun.enterprise.ee.cms.core.SignalReleaseException;
import java.util.logging.Level;

public class ShoalCallBack implements CallBack {
    public void processNotification(Signal signal) {
        ShoalTest.getLogger().log(Level.INFO, "Received Notification of type : " + signal.getClass().getName());
        try {
            signal.acquire();
            ShoalTest.getLogger().log(Level.INFO, "Source Member: " + signal.getMemberToken());
            if (signal instanceof MessageSignal) {
                ShoalTest.getLogger().log(Level.INFO, "Message: " + new String(((MessageSignal) signal).getMessage()));
            }
            signal.release();
        } catch (SignalAcquireException e) {
            ShoalTest.getLogger().log(Level.WARNING, "Exception occured while acquiring signal" + e);
        } catch (SignalReleaseException e) {
            ShoalTest.getLogger().log(Level.WARNING, "Exception occured while releasing signal" + e);
        }
    }
}
