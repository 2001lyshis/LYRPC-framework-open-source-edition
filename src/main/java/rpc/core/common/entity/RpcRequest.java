package rpc.core.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {

    // 请求号 - UUID 唯一生成
    private String requestId;

    // 请求的接口（服务类）名称
    private String interfaceName;

    // 请求的方法名称
    private String methodName;

    // 请求的方法参数
    private Object[] parameters;

    // 请求的方法参数类型
    private Class<?>[] paramTypes;

    // 心跳包标志
    private Boolean heartBeat;

}
