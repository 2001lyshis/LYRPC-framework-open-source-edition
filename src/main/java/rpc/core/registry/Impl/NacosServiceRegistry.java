package rpc.core.registry.Impl;

import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.core.common.util.NacosUtil;
import rpc.core.registry.ServiceRegistry;

import java.net.InetSocketAddress;

public class NacosServiceRegistry implements ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtil.registerService(serviceName, inetSocketAddress);
        } catch (Exception e) {
            logger.error("注册服务时发生错误: ", e);
        }
    }

    @Override
    public void remove(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtil.removeService(serviceName, inetSocketAddress);
        } catch (NacosException e) {
            logger.error("注销服务时发生错误: ", e);
        }
    }
}
