package rpc.core.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SerializerCode {

    JSON(0),
    KRYO(1),
    HESSIAN(2);

    private final int code;
}
