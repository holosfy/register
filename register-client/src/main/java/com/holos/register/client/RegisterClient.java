package com.holos.register.client;


import java.util.UUID;

/**
 * 在服务上被创建和启动，负责跟register-server进行通信
 */
public class RegisterClient {

    private String serviceInstanceId;

    public RegisterClient(){
        this.serviceInstanceId = UUID.randomUUID().toString().replace("-","");
    }

    public void start(){
        new RegisterClientWorker(serviceInstanceId).start();
    }
}
