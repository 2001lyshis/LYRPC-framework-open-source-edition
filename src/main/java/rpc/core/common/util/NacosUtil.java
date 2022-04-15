package rpc.core.common.util;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.common.utils.ConcurrentHashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.core.common.entity.RpcContext;
import rpc.core.common.enumeration.RpcError;
import rpc.core.common.excption.RpcException;


import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;

public class NacosUtil {
    private static final Logger logger = LoggerFactory.getLogger(NacosUtil.class);

    private static final NamingService namingService;
    private static final Set<String> serviceNames = new ConcurrentHashSet<>();


    private static final String SERVER_ADDR = RpcContext.NACOS_SERVER_ADD;
    static {
        namingService = getNacosNamingService();
    }

    private static NamingService getNacosNamingService() {
        try {
            return NamingFactory.createNamingService(SERVER_ADDR);
        } catch (Exception e) {
            logger.error("连接Nacos时发生错误: ", e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    public static void registerService(String serviceName, InetSocketAddress address) throws NacosException {
        try {
            namingService.registerInstance(serviceName, address.getHostName(), address.getPort());
        } catch (NacosException e) {
            logger.error("Nacos连接失败");
        }
        serviceNames.add(serviceName);
    }


    public static List<Instance> getAllInstance(String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }

    public static void removeService(String serviceName, InetSocketAddress inetSocketAddress) throws NacosException {
        List<Instance> instances = namingService.getAllInstances(serviceName);
        String ip = inetSocketAddress.getAddress().toString();
        int port = inetSocketAddress.getPort();
        for(Instance instance : instances) {
            if(ip.equals(instance.getIp()) && port == instance.getPort()) {
                namingService.deregisterInstance(serviceName, instance);
                return;
            }
        }
    }
}
