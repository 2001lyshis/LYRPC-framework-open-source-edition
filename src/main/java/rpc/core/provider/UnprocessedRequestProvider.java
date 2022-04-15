package rpc.core.provider;

import rpc.core.common.entity.RpcResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  这是一个CompletableFuture包装的Rpc-Request的ConcurrentHashMap包装类--简称request容器
 *  将未处理的request暂时存放在这里, 当handler调用@complete方法时，将得到的response的request删除
 *
 *  2022/4/12
 */

public class UnprocessedRequestProvider {

    private static ConcurrentHashMap<String, CompletableFuture<RpcResponse>> map = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse> future) {
        map.put(requestId, future);
    }

    public void remove(String requestId) {
        map.remove(requestId);
    }

    public void complete(RpcResponse response) {
        // 因为使用的是HashMap,Key值一样会造成覆盖
        CompletableFuture<RpcResponse> future = map.remove(response.getAckId());
        if(future != null) {
            future.complete(response);
        } else {
            throw new IllegalStateException();
        }
    }
}
