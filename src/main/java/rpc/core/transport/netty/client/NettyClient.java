package rpc.core.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.core.common.entity.RpcRequest;
import rpc.core.common.entity.RpcResponse;
import rpc.core.common.enumeration.RpcError;
import rpc.core.common.excption.RpcException;
import rpc.core.common.factory.SingletonFactory;
import rpc.core.common.loadbalancer.LoadBalancer;
import rpc.core.common.loadbalancer.RoundRobinLoadBalancer;
import rpc.core.common.serializer.CommonSerializer;
import rpc.core.provider.UnprocessedRequestProvider;
import rpc.core.transport.RpcClient;
import rpc.core.registry.Impl.NacosServiceDiscovery;
import rpc.core.registry.ServiceDiscovery;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

public class NettyClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private static final EventLoopGroup group;
    private static final Bootstrap bootstrap;

    static {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class);
    }

    private final ServiceDiscovery serviceDiscovery;
    private final CommonSerializer serializer;
    private final UnprocessedRequestProvider unprocessedRequestProvider;

    public NettyClient(){
        this(CommonSerializer.DEFAULT_SERIALIZER, new RoundRobinLoadBalancer());
    }
    public NettyClient(LoadBalancer loadBalancer) {
        this(CommonSerializer.DEFAULT_SERIALIZER, loadBalancer);
    }
    public NettyClient(Integer serializer){
        this(serializer, new RoundRobinLoadBalancer());
    }
    public NettyClient(Integer serializer, LoadBalancer loadBalancer) {
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.serializer = CommonSerializer.getByCode(serializer);
        this.unprocessedRequestProvider = SingletonFactory.getInstance(UnprocessedRequestProvider.class);
    }

    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {
        if(serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        CompletableFuture<RpcResponse> future = new CompletableFuture<>();
        try {
            String serviceName = rpcRequest.getInterfaceName() + "@" + rpcRequest.getMethodName();
            InetSocketAddress address = serviceDiscovery.lookupService(serviceName);
            Channel channel = ChannelProvider.get(address, serializer);
            if (channel == null || !channel.isActive()) {
                group.shutdownGracefully();
                return null;
            }
            unprocessedRequestProvider.put(rpcRequest.getRequestId(), future);
            channel.writeAndFlush(rpcRequest);
        } catch (InterruptedException e) {
            unprocessedRequestProvider.remove(rpcRequest.getRequestId());
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        } catch (RpcException | NullPointerException e) {
            logger.error(RpcError.SERVICE_NOT_FOUND.getMessage());
            // 此处返回空值防止阻塞后续请求
            return null;
        }
        return future;
    }


}

