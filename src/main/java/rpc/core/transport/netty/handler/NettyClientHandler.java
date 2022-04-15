package rpc.core.transport.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.core.common.entity.RpcResponse;
import rpc.core.common.factory.SingletonFactory;
import rpc.core.handler.RpcClientHandler;
import rpc.core.transport.netty.client.NettyClient;

import java.io.IOException;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    private final RpcClientHandler rpcClientHandler;


    public NettyClientHandler() {
        this.rpcClientHandler = SingletonFactory.getInstance(RpcClientHandler.class);

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) throws Exception {
        try {
            rpcClientHandler.handle(response);
        } finally {
            ReferenceCountUtil.release(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        logger.error("过程调用时有错误发生:");
        if(cause instanceof IOException) {
            logger.error("远程主机强迫关闭了一个现有的连接");
            ctx.close();
        } else {
            cause.printStackTrace();
            ctx.close();
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
            ctx.close();
    }

}
