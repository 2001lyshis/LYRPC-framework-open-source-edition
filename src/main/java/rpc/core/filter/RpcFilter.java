package rpc.core.filter;

import rpc.core.common.entity.RpcRequest;
import rpc.core.common.entity.RpcResponse;
import rpc.core.common.excption.RpcException;

import java.io.IOException;

public interface RpcFilter {

    void doFilter(RpcRequest request, RpcResponse response, RpcFilterChain filterChain) throws IOException, RpcException;

}
