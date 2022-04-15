package rpc.core.transport;

import io.netty.channel.Channel;
import rpc.core.common.entity.RpcRequest;
import rpc.core.common.serializer.CommonSerializer;

import java.net.InetSocketAddress;

public interface RpcClient {

    Object sendRequest(RpcRequest rpcRequest);



}
