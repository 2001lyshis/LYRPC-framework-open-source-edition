package rpc.core.provider;

import rpc.core.common.entity.RpcResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class UnprocessedRequestProvider {

    private static ConcurrentHashMap<String, CompletableFuture<RpcResponse>> map = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse> future) {
        map.putIfAbsent(requestId, future);
    }

    public void remove(String requestId) {
        map.remove(requestId);
    }

    public void complete(RpcResponse response) {
        CompletableFuture<RpcResponse> future = map.remove(response.getAckId());
        if(future != null) {
            future.complete(response);
        } else {
            throw new IllegalStateException();
        }
    }
}
