package rpc.core.transport.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.core.common.entity.RpcRequest;
import rpc.core.common.entity.RpcResponse;
import rpc.core.common.enumeration.PackageType;
import rpc.core.common.enumeration.RpcError;
import rpc.core.common.excption.RpcException;
import rpc.core.common.serializer.CommonSerializer;

import java.util.List;

/**
 *  1. 负责验证request的合法性
 *  2. 负责解码request
 字段	            解释
 Magic Number	    魔数，表识一个 MRF 协议包，0xCAFEBABE
 Package Type	    包类型，标明这是一个调用请求还是调用响应
 Serializer Type	    序列化器类型，标明这个包的数据的序列化方式
 Data Length	        数据字节的长度
 Data Bytes	        传输的对象，通常是一个RpcRequest或RpcClient对象，取决于Package Type字段，对象的序列化方式取决于Serializer Type字段。
 *
 *  2022/3/31
 */

public class CommonDecode extends ReplayingDecoder {

    private static final Logger logger = LoggerFactory.getLogger(CommonDecode.class);

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        int packageCode = byteBuf.readInt();
        Class<?> packageClass;
        if(packageCode == PackageType.REQUEST_PACK.getCode()) {
            packageClass = RpcRequest.class;
        } else if(packageCode == PackageType.RESPONSE_PACK.getCode()) {
            packageClass = RpcResponse.class;
        } else if(packageCode == PackageType.TEST_PACK.getCode() ||  packageCode == PackageType.HEART_BEAT_PACK.getCode()) {
            packageClass = null;
        } else {
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        if(packageClass == null) {
            return;
        }

        int serializerCode = byteBuf.readInt();
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);

        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        Object object = serializer.deserialize(bytes, packageClass);

        list.add(object);
    }
}
