package rpc.core.registry;

import java.net.InetSocketAddress;

public interface ServiceRegistry {

    void register(String serviceName, InetSocketAddress inetSocketAddress);

    void remove(String serviceName, InetSocketAddress inetSocketAddress);
}
