package com.holos.register.client;


import com.holos.register.RegisterRequest;
import com.holos.register.RegisterResponse;
import com.holos.register.heartbeat.HeartbeatRequest;
import com.holos.register.heartbeat.HeartbeatResponse;

import java.util.UUID;

/**
 * 在服务上被创建和启动，负责跟register-server进行通信
 */
public class RegisterClient {

    public static final String SERVICE_NAME = "inventory-service";
    public static final String IP = "192.168.31.207";
    public static final String HOSTNAME = "inventory01";
    public static final int PORT = 9000;
    private static final Long HEARTBEAT_INTERVAL = 30 * 1000L;

    /**
     * 服务实例id
     */
    private String serviceInstanceId;
    /**
     * http通信组件
     */
    private HttpSender httpSender;
    /**
     * 心跳线程
     */
    private HeartbeatWorker heartbeatWorker;
    /**
     * 服务实例是否在运行
     */
    private Boolean isRunning;
    public RegisterClient() {
        this.serviceInstanceId = UUID.randomUUID().toString().replace("-", "");
        this.httpSender = new HttpSender();
        this.heartbeatWorker = new HeartbeatWorker();
        this.isRunning = true;
    }


    public void start() {
        try {
            // 一旦启动了这个组件之后，他就负责在服务上干两个事情
            // 第一个事情，就是开启一个线程向register-server去发送请求，注册这个服务
            // 第二个事情，就是在注册成功之后，就会开启另外一个线程去发送心跳

            // 我们来简化一下这个模型
            // 我们在register-client这块就开启一个线程
            // 这个线程刚启动的时候，第一个事情就是完成注册
            // 如果注册完成了之后，他就会进入一个while true死循环
            // 每隔30秒就发送一个请求去进行心跳
            RegisterWorker registerWorker = new RegisterWorker();
            registerWorker.start();
            // main线程阻塞，等该线程执行结束
            registerWorker.join();

            HeartbeatWorker heartbeatWorker = new HeartbeatWorker();
            heartbeatWorker.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止RegisterClient组件
     *   因为各个工作线程可能都在不断的while循环运行，但是每次执行完一次之后，都会进入休眠的状态，sleep 30秒
     *   如果系统要尽快停止，那么就应该用interrupt打断各个工作线程的休眠，让他们判断是否运行的标志位为false，就立刻退出
     */
    public void shutdown() {
        this.isRunning = false;
        this.heartbeatWorker.interrupt();
    }


    /**
     * 服务注册线程
     */
    private class RegisterWorker extends Thread {

        @Override
        public void run() {
            // 应该是获取当前机器的信息
            // 包括当前机器的ip地址、hostname，以及你配置这个服务监听的端口号
            // 从配置文件里可以拿到
            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setServiceName(SERVICE_NAME);
            registerRequest.setIp(IP);
            registerRequest.setHostname(HOSTNAME);
            registerRequest.setPort(PORT);
            registerRequest.setServiceInstanceId(serviceInstanceId);

            RegisterResponse registerResponse = httpSender.register(registerRequest);

            System.out.println("服务注册的结果是：" + registerResponse.getStatus() + "......");
        }

    }

    /**
     * 心跳线程
     *
     */
    private class HeartbeatWorker extends Thread {

        @Override
        public void run() {
            // 如果说注册成功了，就进入while true死循环
            HeartbeatRequest heartbeatRequest = new HeartbeatRequest();
            heartbeatRequest.setServiceName(SERVICE_NAME);
            heartbeatRequest.setServiceInstanceId(serviceInstanceId);

            HeartbeatResponse heartbeatResponse = null;

            while (true) {
                try {
                    heartbeatResponse = httpSender.heartbeat(heartbeatRequest);
                    System.out.println("心跳的结果为：" + heartbeatResponse.getStatus() + "......");
                    Thread.sleep(HEARTBEAT_INTERVAL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
