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

package com.elasticgrid.amazon.sqs;

import org.rioproject.event.EventDescriptor;
import org.rioproject.event.RemoteServiceEvent;
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
