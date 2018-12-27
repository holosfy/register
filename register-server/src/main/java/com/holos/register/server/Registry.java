package com.holos.register.server;

import java.util.HashMap;
import java.util.Map;

/**
 * 注册表
 */
public class Registry {

    private static Registry instance = new Registry();
    private Registry(){

    }

    /**
     * 核心的内存数据结构：注册表
     * <p>
     * Map：key是服务名称，value是这个服务的所有的服务实例
     * Map<String, ServiceInstance>：key是服务实例id，value是服务实例的信息
     */
    private Map<String,Map<String,ServiceInstance>> registry = new HashMap<String, Map<String, ServiceInstance>>();

    /**
     * 服务注册
     *
     * @param serviceInstance 服务实例
     */
    public void register(ServiceInstance serviceInstance) {
        Map<String, ServiceInstance> serviceInstanceMap =
                registry.get(serviceInstance.getServiceName());

        if (serviceInstanceMap == null) {
            serviceInstanceMap = new HashMap<String, ServiceInstance>();
            registry.put(serviceInstance.getServiceName(), serviceInstanceMap);
        }

        serviceInstanceMap.put(serviceInstance.getServiceInstanceId(),
                serviceInstance);

        System.out.println("服务实例【" + serviceInstance + "】，完成注册......");
        System.out.println("注册表：" + registry);
    }


    /**
     * 获取服务实例
     *
     * @param serviceName       服务名称
     * @param serviceInstanceId 服务实例id
     * @return 服务实例
     */
    public ServiceInstance getServiceInstance(String serviceName,
                                              String serviceInstanceId) {
        Map<String, ServiceInstance> serviceInstanceMap = registry.get(serviceName);
        return serviceInstanceMap.get(serviceInstanceId);
    }

    /**
     * 获取整个注册表
     *
     * @return
     */
    public Map<String, Map<String, ServiceInstance>> getRegistry() {
        return registry;
    }

    /**
     * 从注册表删除一个服务实例
     *
     * @param serviceName
     * @param serviceInstanceId
     */
    public void remove(String serviceName, String serviceInstanceId) {
        System.out.println("服务实例【" + serviceInstanceId + "】，从注册表中进行摘除");
        Map<String, ServiceInstance> serviceInstanceMap = registry.get(serviceName);
        serviceInstanceMap.remove(serviceInstanceId);
    }

    public static Registry getInstance() {
        return instance;
    }

}
