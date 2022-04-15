package rpc.core.registry.Impl;

import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.core.common.enumeration.RpcError;
import rpc.core.common.excption.RpcException;
import rpc.core.common.loadbalancer.LoadBalancer;
import rpc.core.common.loadbalancer.RoundRobinLoadBalancer;
import rpc.core.common.util.NacosUtil;
import rpc.core.registry.ServiceDiscovery;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NacosServiceDiscovery implements ServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceDiscovery.class);

    private final LoadBalancer loadBalancer;

    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        if(loadBalancer == null) {
            this.loadBalancer = new RoundRobinLoadBalancer();
        } else {
            this.loadBalancer = loadBalancer;
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = NacosUtil.getAllInstance(serviceName);
            if(instances.size() == 0) {
                logger.error("没有对应服务:{} ", serviceName);
                return null;
//              throw new RpcException(RpcError.SERVICE_NOT_FOUND);
            }
            Instance instance = loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (Exception e) {
            logger.error("获取服务时发生错误 : ", e);
            return null;
        }
    }




}
