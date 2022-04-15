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

    private Map<String, Method> serviceMap = new ConcurrentHashMap<>();

    @Override
    public <T> void addService(Method service, String serviceName) {
        serviceMap.putIfAbsent(serviceName, service);
        serviceMap.put(serviceName, service);
        logger.info("服务接口: {} -> 注册服务名: {}", service.getDeclaringClass().getSimpleName(), serviceName);
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
