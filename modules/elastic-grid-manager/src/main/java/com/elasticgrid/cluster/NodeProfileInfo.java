package com.elasticgrid.cluster;

import com.elasticgrid.model.NodeProfile;

public class NodeProfileInfo {
    private NodeProfile nodeProfile;
    private String instanceType;
    private int number;

    public NodeProfileInfo(NodeProfile nodeProfile, String instanceType, int number) {
        this.nodeProfile = nodeProfile;
        this.instanceType = instanceType;
        this.number = number;
    }

    public NodeProfile getNodeProfile() {
        return nodeProfile;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public int getNumber() {
        return number;
    }
}
