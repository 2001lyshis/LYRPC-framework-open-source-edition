package rpc.core.transport.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import rpc.core.common.entity.RpcRequest;
import rpc.core.common.enumeration.PackageType;
import rpc.core.common.serializer.CommonSerializer;

/**
 *  自定义编码拦截器, 将数据打包成符合规定的request
 *
 *  DO: 末尾做校验和 ?
 *  这应该是TCP协议做的事情，我们应用层处于高层就不需要再做校验和了
 *
 字段	解释
 Magic Number	    魔数，表识一个 MRF 协议包，0xCAFEBABE
 Package Type	    包类型，标明这是一个调用请求还是调用响应
 Serializer Type	    序列化器类型，标明这个包的数据的序列化方式
 Data Length	        数据字节的长度
 Data Bytes	        传输的对象，通常是一个RpcRequest或RpcClient对象，取决于Package Type字段，对象的序列化方式取决于Serializer Type字段。

 *  2022/3/31
 */

public class CommonEncode extends MessageToByteEncoder {


    private final CommonSerializer serializer;

    public CommonEncode(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {

        if (o instanceof RpcRequest) {
            byteBuf.writeInt(PackageType.REQUEST_PACK.getCode());
        } else {
            byteBuf.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        byteBuf.writeInt(serializer.getCode());
        byte[] bytes = serializer.serializer(o);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

    }
}
