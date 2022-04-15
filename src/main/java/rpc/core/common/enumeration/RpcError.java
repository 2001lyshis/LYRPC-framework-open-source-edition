package rpc.core.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RpcError {

    UNKNOWN_ERROR("未知的错误"),
    SERVICE_SCAN_PACKAGE_NOT_FOUND("未找到包"),
    CLIENT_CONNECT_SERVER_FAILURE("客户端连接服务器失败"),
    SERVICE_INVOCATION_FAILURE("服务器调用失败"),
    SERVICE_NOT_FOUND("找不到对应服务"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("注册的服务未实现接口"),
    UNKNOWN_PROTOCOL("不支持的协议"),
    UNKNOWN_SERIALIZER("不支持的序列化器"),
    UNKNOWN_PACKAGE_TYPE("不支持的数据包类型"),
    SERIALIZER_NOT_FOUND("找不到序列化器"),
    RESPONSE_NOT_MATCH("响应与请求号不匹配"),
    FAILED_TO_CONNECT_TO_SERVICE_REGISTRY("连接注册中心失败"),
    REGISTER_SERVICE_FAILED("注册服务失败"),
    START_REQUEST_ANNOTATION("缺少启动类注解"),
    REQUEST_TIMEOUT("请求超时"),
    REQUEST_NULL_POINT("空请求");
    private final String message;
}
