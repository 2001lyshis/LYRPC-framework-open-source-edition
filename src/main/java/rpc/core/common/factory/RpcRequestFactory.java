package rpc.core.common.factory;

import rpc.core.common.entity.RpcRequest;

import java.util.UUID;

public class RpcRequestFactory {

    public static RpcRequest createRpcRequest(String requestId, String interfaceName, String methodName, Object[] args, Class<?>[] paramTypes,boolean heartBeat) {
        return new RpcRequest(requestId, interfaceName, methodName, args, paramTypes, heartBeat);
    }
    public static RpcRequest createRpcRequest(String interfaceName, String methodName, Object[] args, Class<?>[] paramTypes,boolean heartBeat) {
        return createRpcRequest(UUID.randomUUID().toString(), interfaceName, methodName, args, paramTypes, heartBeat);
    }
    public static RpcRequest createRpcRequest(String interfaceName, String methodName, Object[] args, Class<?>[] paramTypes) {
        return createRpcRequest(UUID.randomUUID().toString(), interfaceName, methodName, args, paramTypes, false);
    }
    public static RpcRequest createRpcRequest(String interfaceName) {
        return createRpcRequest(UUID.randomUUID().toString(), interfaceName, null, null, null, false);
    }
    public static RpcRequest createRpcRequest(String requestId, String interfaceName) {
        return createRpcRequest(requestId, interfaceName, null, null, null, false);
    }
    public static RpcRequest createRpcRequest(boolean heartBeat) {
        return createRpcRequest(UUID.randomUUID().toString(), null, null, null, null, true);
    }
}
