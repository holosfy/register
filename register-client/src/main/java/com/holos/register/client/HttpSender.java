package com.holos.register.client;

/**
 * 发送Http请求组件
 */
public class HttpSender {


    public RegisterResponse register(RegisterRequest request){
        System.out.println("服务实例【" + request + "】，发送请求进行注册......");
        RegisterResponse response = new RegisterResponse();
        response.setStatus(RegisterResponse.SUCCESS);

        return response;
    }

    public HeartbeatResponse heartbeat(HeartbeatRequest request){
        System.out.println("服务实例【" + request.getServiceInstanceId() + "】，发送请求进行心跳......");

        HeartbeatResponse response = new HeartbeatResponse();
        response.setStatus(RegisterResponse.SUCCESS);

        return response;
    }
}
