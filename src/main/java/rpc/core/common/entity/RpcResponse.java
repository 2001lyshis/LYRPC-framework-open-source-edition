package rpc.core.common.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class RpcResponse<T> implements Serializable {

    // 响应请求号 UUID - 唯一生成
    private String ackId;

     // 相应状态码
    private Integer statusCode;

    // 相应数据
    private T data;

}
