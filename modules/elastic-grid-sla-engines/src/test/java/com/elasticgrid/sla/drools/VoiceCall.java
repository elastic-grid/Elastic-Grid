package com.elasticgrid.sla.drools;

import java.util.Date;

public class VoiceCall {
    private String originNumber;
    private String destinationNumber;
    private Date callDateTime;
    private long callDuration;          // in milliseconds

    public String getOriginNumber() {
        return originNumber;
    }

    public void setOriginNumber(String originNumber) {
        this.originNumber = originNumber;
    }

    public String getDestinationNumber() {
        return destinationNumber;
    }

    public void setDestinationNumber(String destinationNumber) {
        this.destinationNumber = destinationNumber;
    }

    public Date getCallDateTime() {
        return callDateTime;
    }

    public void setCallDateTime(Date callDateTime) {
        this.callDateTime = callDateTime;
    }

    public long getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(long callDuration) {
        this.callDuration = callDuration;
    }
}
