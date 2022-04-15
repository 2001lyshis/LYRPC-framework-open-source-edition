package rpc.core.filter;

import rpc.core.common.entity.RpcRequest;
import rpc.core.common.entity.RpcResponse;
import rpc.core.common.excption.RpcException;

import java.io.IOException;

public interface RpcFilterChain {
    void doFilter(RpcRequest request, RpcResponse response) throws IOException, RpcException;
}
