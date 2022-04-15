package rpc.core.provider;

import java.lang.reflect.Method;

/**
 *  服务接口注册表
 */

public interface ServiceProvider {

    <T> void addService(Method service, String serviceName);

    void removeService(String serviceName);

    Method getService(String serviceName);
}
