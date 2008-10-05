package com.elasticgrid.grid.discovery.shoal;

import com.sun.enterprise.ee.cms.core.CallBack;
import com.sun.enterprise.ee.cms.core.GMSConstants;
import com.sun.enterprise.ee.cms.core.GMSException;
import com.sun.enterprise.ee.cms.core.GMSFactory;
import com.sun.enterprise.ee.cms.core.GroupHandle;
import com.sun.enterprise.ee.cms.core.GroupManagementService;
import com.sun.enterprise.ee.cms.core.Signal;
import com.sun.enterprise.ee.cms.core.JoinNotificationSignal;
import com.sun.enterprise.ee.cms.core.SignalAcquireException;
import com.sun.enterprise.ee.cms.core.SignalReleaseException;
import com.sun.enterprise.ee.cms.impl.client.JoinNotificationActionFactoryImpl;
import com.sun.enterprise.ee.cms.impl.client.MessageActionFactoryImpl;
import com.sun.enterprise.ee.cms.impl.client.PlannedShutdownActionFactoryImpl;
import com.elasticgrid.model.NodeProfile;
import org.testng.annotations.Test;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

public class ShoalTest {
    final static Logger logger = Logger.getLogger("ShoalTest");

    @Test
    public void testIt() throws InterruptedException, GMSException {
        String serverName = "test-" + System.nanoTime();     // uniquely identifies this instance in the cluster
        String groupName = "elastic-grid-cluster-test";
        final GroupManagementService gms = (GroupManagementService)
                GMSFactory.startGMSModule(serverName, groupName, GroupManagementService.MemberType.CORE, null);
        try {
//            CallBack callback = new ShoalCallBack();
            gms.addActionFactory(new JoinNotificationActionFactoryImpl(new CallBack() {
                public void processNotification(Signal signal) {
                    try {
                        signal.acquire();
                        JoinNotificationSignal joinSignal = (JoinNotificationSignal) signal;
                        Map<Serializable, Serializable> details = joinSignal.getMemberDetails();
                        System.out.println("Details: " + details);
                        signal.release();
                    } catch (SignalAcquireException e) {
                        e.printStackTrace();
                    } catch (SignalReleaseException e) {
                        e.printStackTrace();
                    }
                }
            }));
//            gms.addActionFactory(new PlannedShutdownActionFactoryImpl(callback));
//            gms.addActionFactory(new MessageActionFactoryImpl(callback), "SimpleSampleComponent");
            gms.join();
            gms.updateMemberDetails(serverName, "profile", NodeProfile.MONITOR);
        } catch (GMSException e) {
            e.printStackTrace();
        }

//        GroupHandle gh = gms.getGroupHandle();
//        logger.log(Level.INFO, "Sending messages...");
//        for(int i = 0; i<=10; i++ ){
//            gh.sendMessage("SimpleSampleComponent",
//                    MessageFormat.format("Message {0}from server {1}", i, serverName).getBytes());
//        }

        Thread.sleep(10000);
        gms.shutdown(GMSConstants.shutdownType.INSTANCE_SHUTDOWN);
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void main(String[] args) throws GMSException, InterruptedException {
        new ShoalTest().testIt();
    }
}
