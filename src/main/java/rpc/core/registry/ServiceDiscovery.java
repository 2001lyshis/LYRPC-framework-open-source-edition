package rpc.core.registry;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public interface ServiceDiscovery {

    InetSocketAddress lookupService(String serviceName);

}
