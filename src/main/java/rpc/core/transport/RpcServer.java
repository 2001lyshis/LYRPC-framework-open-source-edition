package rpc.core.transport;

import rpc.core.provider.ServiceProvider;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;

/**
 * 服务器顶级接口
 */

public interface RpcServer {

    @Deprecated
    void run();

    void start();

    <T> void publishService(Method service, String serviceName, ServiceProvider provider);

    void shutdown();

}
