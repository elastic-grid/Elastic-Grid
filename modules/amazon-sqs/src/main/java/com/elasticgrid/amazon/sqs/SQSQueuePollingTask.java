/**
 * Elastic Grid
 * Copyright (C) 2008-2010 Elastic Grid, LLC.
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
package com.elasticgrid.amazon.sqs;

import com.xerox.amazonws.sqs2.Message;
import com.xerox.amazonws.sqs2.MessageQueue;
import com.xerox.amazonws.sqs2.SQSException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Task polling SQS queue.
 * @author Jerome Bernard
*/
class SQSQueuePollingTask implements Runnable {
    private final MessageQueue queue;
    private final SQSServiceBeanAdapter bean;
    private final Logger logger = Logger.getLogger(getClass().getName());

    public SQSQueuePollingTask(SQSServiceBeanAdapter bean, MessageQueue queue) {
        this.bean = bean;
        this.queue = queue;
        logger.info("Starting active polling of queue " + SQSUtils.getQueueName(queue));
    }

    public void run() {
        try {
            logger.log(Level.FINER, "Polling queue {0}...", SQSUtils.getQueueName(queue));
            for (;;) {
                // receive message
                Message message = queue.receiveMessage();
                if (message == null)
                    continue;
                String messageID = message.getMessageId();
                String receiptHandle = message.getReceiptHandle();
                String body = message.getMessageBody();
                String md5 = message.getBodyMD5();
                SQSEvent event = new SQSEvent(bean.getExportedProxy(), messageID, receiptHandle, body, md5);
                // ask the bean to handle the event
                bean.handle(event);
                // delete message from queue
                queue.deleteMessage(message);
            }
        } catch (SQSException e) {
            logger.log(Level.SEVERE, "Could not receive SQS message", e);
            e.printStackTrace();
        } catch (RemoteException e) {
            logger.log(Level.SEVERE, "Unexpected remote exception when processing SQS message", e);
            e.printStackTrace();
        }
    }
}
