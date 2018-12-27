package com.holos.register.client;

/**
 * 心跳请求
 *  发送服务id
 */
public class HeartbeatRequest {
    private String serviceInstanceId;

    public String getServiceInstanceId() {
        return serviceInstanceId;
    }

    public void setServiceInstanceId(String serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }
}
