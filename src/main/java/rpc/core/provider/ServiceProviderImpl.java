package rpc.core.provider;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.core.common.enumeration.RpcError;
import rpc.core.common.excption.RpcException;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceProviderImpl implements ServiceProvider {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);

    private static final Map<String, Method> serviceMap = new ConcurrentHashMap<>();
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    @Override
    public <T> void addService(Method service, String serviceName) {
        if(registeredService.contains(serviceName))
            return;
        registeredService.add(serviceName);
        serviceMap.put(serviceName, service);
        logger.info("服务接口: {} -> 注册服务名: {}", service.getDeclaringClass().getSimpleName(), serviceName);
    }

    @Override
    public void removeService(String serviceName) {
        if(!registeredService.contains(serviceName))
            return;
        registeredService.remove(serviceName);
        serviceMap.remove(serviceName);
        logger.info("已注销服务名: {}", serviceName);
    }

    @Override
    public Method getService(String serviceName) {
        Method service = serviceMap.get(serviceName);
        if(service == null) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
