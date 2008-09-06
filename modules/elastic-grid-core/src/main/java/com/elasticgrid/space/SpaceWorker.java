/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This file is part of Elastic Grid.
 * 
 * Elastic Grid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * Elastic Grid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Elastic Grid.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elasticgrid.space;

import net.jini.config.Configuration;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.lease.LeaseRenewalManager;
import net.jini.space.JavaSpace05;
import org.rioproject.associations.AssociationProxyUtil;
import org.rioproject.core.jsb.ServiceBeanContext;
import org.rioproject.jsb.ServiceBeanAdapter;

import java.io.IOException;
import java.io.Serializable;
import static java.lang.String.format;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.elasticgrid.Task;

/**
 * Worker based on {@link net.jini.space.JavaSpace05}.
 * @author Jerome Bernard
 */
public class SpaceWorker extends ServiceBeanAdapter implements Runnable {
    private Entry template;
    private long pollTimeout, txnTimeout;
    private AtomicBoolean started = new AtomicBoolean(false);
    private JavaSpace05 space;
    private TransactionManager txnManager;
    private LeaseRenewalManager lrm;
    private ExecutorService threadExecutor = Executors.newSingleThreadExecutor();
    private Logger logger = Logger.getLogger(getClass().getName());

    public void initialize(ServiceBeanContext context) throws Exception {
        super.initialize(context);
        Configuration config = context.getConfiguration();
        pollTimeout = (Long) config.getEntry(
                "com.elasticgrid.space.SpaceWorker",
                "pollTimeout", Long.class);
        txnTimeout = (Long) config.getEntry(
                "com.elasticgrid.space.SpaceWorker",
                "txnTimeout", Long.class);
        template = (TaskEntry) config.getEntry(
                "com.elasticgrid.space.SpaceWorker",
                "taskTemplate", TaskEntry.class, new TaskEntry());
    }

    public void advertise() throws IOException {
        started.set(true);
        lrm = new LeaseRenewalManager();
        threadExecutor.execute(this);
        super.advertise();
    }

    public void run() {
        TaskEntry entry;
        Transaction txn = null;
        while (started.get()) {
            try {
                Transaction.Created txnCreated = TransactionFactory.create(txnManager, txnTimeout);
                txn = txnCreated.transaction;
                lrm.renewFor(txnCreated.lease, pollTimeout, null);
                logger.info("Trying to take task...");
                entry = (TaskEntry) space.takeIfExists(template, txn, pollTimeout);
                if (entry == null) {
                    try {
                        txn.abort();
                    } catch (TransactionException e) {
                        logger.log(Level.WARNING, "Can't abort unneeded transaction", e);
                    }
                    Thread.sleep(pollTimeout);
                    continue;
                }
                logger.info("Task found. Executing it...");
                Entry result = execute(entry);
                space.write(result, txn, Lease.FOREVER);
                txn.commit();
                logger.info("... done processing!");
            } catch (RemoteException e) {
                logger.log(Level.WARNING, "Can't execute task", e);
                try {
                    if (txn != null) {
                        txn.abort();
                    }
                } catch (Exception te) {
                    logger.log(Level.SEVERE, "Can't abort transaction", e);
                }
            } catch (UnusableEntryException e) {
                logger.log(Level.SEVERE, "Can't use task", e);
            } catch (TransactionException e) {
                logger.log(Level.SEVERE, "Can't use transaction", e);
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Can't stop thread", e);
            } catch (LeaseDeniedException e) {
                logger.log(Level.SEVERE, "Can't create lease", e);
            }
        }
    }

    public Entry execute(TaskEntry<Serializable> entry) throws RemoteException {
        try {
            String taskID = entry.getTaskID();
            Task<Serializable> task = entry.getTask();
            logger.info(format("Executing task %s[%s]", task.getClass().getName(), taskID));
            return new TaskResult<Serializable>(taskID, task.call());
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Unexpected remote exception", t);
            throw new RemoteException("Unexpected remote exception", t);
        }
    }

    public void stop(boolean force) {
        threadExecutor.shutdown();
        started.set(false);
        if (lrm != null)
            lrm.clear();
        super.stop(force);
    }

    /** Injected by Rio. */
    public void setSpace(JavaSpace05 space) {
        this.space = space;
    }

    /** Injected by Rio. */
    public void setTransactionManager(TransactionManager txnManager) {
        this.txnManager = AssociationProxyUtil.getService(txnManager);
    }
}