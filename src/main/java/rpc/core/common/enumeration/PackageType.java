package rpc.core.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  发送的包的类型
 *  
 *  2022/4/8
 */
@AllArgsConstructor
@Getter
public enum PackageType {

    REQUEST_PACK(0),
    RESPONSE_PACK(1),
    HEART_BEAT_PACK(2),
    TEST_PACK(3);

    private final int code;
}
