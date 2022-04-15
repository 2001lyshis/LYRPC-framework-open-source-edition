package rpc.core.common.enumeration;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *       200 调用成功
 *       500 调用失败
 *       404 未找到指定方法或类
 */
@AllArgsConstructor
@Getter
public enum  ResponseCode {

    SUCCESS(200, "调用成功"),
    PROCESSING(100, "处理中"),
    FAIL(500, "调用失败"),
    TIMEOUT(20001,"请求超时"),
    METHOD_NOT_FOUND(404, "未找到指定方法"),
    CLASS_NOT_FOUND(404, "未找到指定类"),
    NO_PERMISSION(401, "没有权限");

    private final int code;
    private final String message;
}
