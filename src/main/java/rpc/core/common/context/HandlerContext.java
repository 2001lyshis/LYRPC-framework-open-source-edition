package rpc.core.common.context;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rpc.core.provider.ServiceProvider;

import java.net.InetSocketAddress;


@Data
public class HandlerContext {
    public InetSocketAddress localAddress;
    public InetSocketAddress remoteAddress;
    public Channel channel;
    public String serviceProviderName = RpcContext.DEFAULT_PROVIDER_NAME;
    public ServiceProvider provider;
    public String serviceName;
}

