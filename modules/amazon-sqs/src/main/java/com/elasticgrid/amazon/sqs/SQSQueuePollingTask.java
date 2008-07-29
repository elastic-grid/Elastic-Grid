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
