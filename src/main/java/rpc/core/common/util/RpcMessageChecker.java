package rpc.core.common.util;

import rpc.core.common.entity.RpcRequest;
import rpc.core.common.entity.RpcResponse;
import rpc.core.common.enumeration.ResponseCode;
import rpc.core.common.enumeration.RpcError;
import rpc.core.common.excption.RpcException;

/**
 *  这是request和response的检查工具类
 *
 *  2022/4/10
 */
public class RpcMessageChecker {
    public static final String INTERFACE_NAME = "interfaceName : ";

    private RpcMessageChecker(){}

    public static void receiveCheck(RpcRequest request, RpcResponse response) {
        if (response == null) {
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + request.getInterfaceName());
        }

        if(!request.getRequestId().equals(response.getAckId())) {
            throw new RpcException(RpcError.RESPONSE_NOT_MATCH, INTERFACE_NAME + request.getRequestId());
        }

        if(response.getStatusCode() == null || !response.getStatusCode().equals(ResponseCode.SUCCESS.getCode())) {
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + request.getInterfaceName());
        }
    }

    public static void sendCheck(RpcRequest request) {
        if (request == null) {
            throw new RpcException(RpcError.REQUEST_NULL_POINT);
        }
    }
}
