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
        response.setMessage(ResponseCode.PROCESSING.getMessage());
        return response;
    }

    public static <T> RpcResponse<T> addResult(RpcResponse response, T data) {
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setMessage(ResponseCode.SUCCESS.getMessage());
        response.setData(data);
        return response;
    }

    public static <T> RpcResponse<T> success(T data, String ackId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setAckId(ackId);
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setMessage(ResponseCode.SUCCESS.getMessage());
        response.setData(data);
        return response;
    }

    public static <T> RpcResponse<T> fail(String ackId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setAckId(ackId);
        response.setStatusCode(ResponseCode.FAIL.getCode());
        response.setMessage(ResponseCode.FAIL.getMessage());
        return response;
    }

    public static <T> RpcResponse<T> fail(String msg ,String ackId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setAckId(ackId);
        response.setStatusCode(ResponseCode.FAIL.getCode());
        response.setMessage(msg);
        return response;
    }

    public static <T> RpcResponse<T> timeOut() {
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(ResponseCode.TIMEOUT.getCode());
        response.setMessage(ResponseCode.TIMEOUT.getMessage());
        return response;
    }

    public static <T> RpcResponse<T> permissionDeny(String ackId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setAckId(ackId);
        response.setStatusCode(ResponseCode.NO_PERMISSION.getCode());
        response.setMessage(ResponseCode.NO_PERMISSION.getMessage());
        return response;
    }

    public static <T> RpcResponse<T> classNotFound(String ackId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setAckId(ackId);
        response.setStatusCode(ResponseCode.CLASS_NOT_FOUND.getCode());
        response.setMessage(ResponseCode.CLASS_NOT_FOUND.getMessage());
        return response;
    }

    public static <T> RpcResponse<T> methodNotFound(String ackId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setAckId(ackId);
        response.setStatusCode(ResponseCode.METHOD_NOT_FOUND.getCode());
        response.setMessage(ResponseCode.METHOD_NOT_FOUND.getMessage());
        return response;
    }
}
