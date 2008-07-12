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

import org.rioproject.event.RemoteServiceEvent;
import org.rioproject.event.EventDescriptor;

import java.io.Serializable;

/**
 * @author Jerome Bernard
 */
public class SQSEvent extends RemoteServiceEvent implements Serializable {
    /** A unique id number for the hello event **/
    public static final long ID = 9999999999L;
    /** Holds the property for the time the event was created */
    private long when;
    /** Holds the message ID property */
    private String messageID;
    /** Holds the message ID property */
    private String messageReceiptHandle;
    /** Holds the message ID property */
    private String messageBody;
    /** Holds the message ID property */
    private String messageBodyMD5;

    public SQSEvent(Object source) {
        this(source, null, null, null, null);
    }

    public SQSEvent(Object source, String messageID, String messageReceiptHandle, String messageBody, String messageBodyMD5) {
        super(source);
        this.messageID = messageID;
        this.messageReceiptHandle = messageReceiptHandle;
        this.messageBody = messageBody;
        this.messageBodyMD5 = messageBodyMD5;
        when = System.currentTimeMillis();
    }

    public long getWhen() {
        return when;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getMessageReceiptHandle() {
        return messageReceiptHandle;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public String getMessageBodyMD5() {
        return messageBodyMD5;
    }
    
    public static EventDescriptor getEventDescriptor() {
        return (new EventDescriptor(SQSEvent.class, ID));
    }

    public String toString() {
        return "SQSEvent{" +
                "when=" + when +
                ", messageID='" + messageID + '\'' +
                ", messageReceiptHandle='" + messageReceiptHandle + '\'' +
                ", messageBody='" + messageBody + '\'' +
                ", messageBodyMD5='" + messageBodyMD5 + '\'' +
                '}';
    }
}
