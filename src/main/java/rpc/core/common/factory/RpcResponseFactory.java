package rpc.core.common.factory;

import rpc.core.common.entity.RpcResponse;
import rpc.core.common.enumeration.ResponseCode;

/**
 *  用来生成响应response 的工厂类
 *
 *  2022/4/8
 */

public class RpcResponseFactory {
    public static <T> RpcResponse<T> createResponse(String ackID) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setAckId(ackID);
        response.setStatusCode(ResponseCode.PROCESSING.getCode());
        return response;
    }

    @SuppressWarnings("unchecked")
    public static <T> RpcResponse<T> addResult(RpcResponse response, T data) {
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setData(data);
        return response;
    }

    public static <T> RpcResponse<T> success(T data, String ackId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setAckId(ackId);
        response.setStatusCode(ResponseCode.SUCCESS.getCode());

        response.setData(data);
        return response;
    }

    public static <T> RpcResponse<T> fail(String ackId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setAckId(ackId);
        response.setStatusCode(ResponseCode.FAIL.getCode());
        return response;
    }

    public static <T> RpcResponse<T> fail(String msg ,String ackId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setAckId(ackId);
        response.setStatusCode(ResponseCode.FAIL.getCode());
        return response;
    }

    public static <T> RpcResponse<T> timeOut() {
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(ResponseCode.TIMEOUT.getCode());
        return response;
    }

    public static <T> RpcResponse<T> permissionDeny(String ackId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setAckId(ackId);
        response.setStatusCode(ResponseCode.NO_PERMISSION.getCode());
        return response;
    }

    public static <T> RpcResponse<T> classNotFound(String ackId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setAckId(ackId);
        response.setStatusCode(ResponseCode.CLASS_NOT_FOUND.getCode());
        return response;
    }

    public static <T> RpcResponse<T> methodNotFound(String ackId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setAckId(ackId);
        response.setStatusCode(ResponseCode.METHOD_NOT_FOUND.getCode());
        return response;
    }
}
