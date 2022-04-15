package rpc.core.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SerializerCode {
    KRYO(0);
    private final int code;
}
