package com.holos.register.server;

import java.util.Map;

/**
 * 微服务存活状态监控组件
 */
public class ServiceAliveMonitor {
    /**
     * 检查服务实例是否存活的间隔
     */
    private static final Long CHECK_ALIVE_INTERVAL = 60 * 1000L;

    /**
     * 负责监控微服务存活状态的后台线程
     */
    private Daemon daemon = new Daemon();

    /**
     * 启动后台线程
     */
    public void start() {
        daemon.start();
    }

    /**
     * 负责监控微服务存活状态的后台线程
     *
     * @author zhonghuashishan
     */
    private class Daemon extends Thread {

        private Registry registry = Registry.getInstance();

        @Override
        public void run() {
            Map<String, Map<String, ServiceInstance>> registryMap = null;

            while (true) {
                try {
                    registryMap = registry.getRegistry();

                    for (String serviceName : registryMap.keySet()) {
                        Map<String, ServiceInstance> serviceInstanceMap =
                                registryMap.get(serviceName);

                        for (ServiceInstance serviceInstance : serviceInstanceMap.values()) {
                            // 说明服务实例距离上一次发送心跳已经超过90秒了
                            // 认为这个服务就死了
                            // 从注册表中摘除这个服务实例
                            if (!serviceInstance.isAlive()) {
                                registry.remove(serviceName, serviceInstance.getServiceInstanceId());
                            }
                        }
                    }

                    Thread.sleep(CHECK_ALIVE_INTERVAL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
