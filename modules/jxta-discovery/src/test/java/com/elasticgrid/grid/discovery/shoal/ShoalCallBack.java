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
