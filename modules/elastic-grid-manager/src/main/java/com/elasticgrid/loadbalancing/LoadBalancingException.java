package com.elasticgrid.loadbalancing;

/**
 * Load-balancing exception.
 * @author Jerome Bernard
 */
public class LoadBalancingException extends Exception {
    public LoadBalancingException(String message) {
        super(message);
    }

    public LoadBalancingException(String message, Throwable cause) {
        super(message, cause);
    }
}
