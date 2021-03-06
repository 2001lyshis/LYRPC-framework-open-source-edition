package rpc.core.intercepter;

import rpc.core.common.context.HandlerContext;
import rpc.core.common.entity.RpcRequest;
import rpc.core.common.entity.RpcResponse;

public interface RpcInterceptor {
    default boolean preHandle(RpcRequest request, RpcResponse response, HandlerContext context) {
        return true;
    }
}
