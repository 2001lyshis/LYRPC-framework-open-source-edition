package rpc.core.transport.netty.handler;

import io.netty.handler.timeout.IdleStateEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.core.common.context.HandlerContext;

import rpc.core.common.entity.RpcResponse;
import rpc.core.handler.RpcSeverHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

import rpc.core.common.entity.RpcRequest;


import java.io.IOException;
import java.net.InetSocketAddress;


public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        try {
            if(rpcRequest.getHeartBeat()) {
                logger.info("Client heart beating..");
                return;
            }
            HandlerContext handlerContext = new HandlerContext();
            handlerContext.setChannel(ctx.channel());
            handlerContext.setServiceName(rpcRequest.getInterfaceName());
            handlerContext.setLocalAddress((InetSocketAddress) ctx.channel().localAddress());
            handlerContext.setRemoteAddress((InetSocketAddress) ctx.channel().remoteAddress());
            RpcSeverHandler requestHandler = new RpcSeverHandler(handlerContext);
            RpcResponse response = requestHandler.handle(rpcRequest);
            if(response == null) {
                return;
            }
            if(ctx.channel().isActive() && ctx.channel().isWritable()) {
                ctx.writeAndFlush(response);
            } else {
                logger.error("channel can not write");
            }
        } finally {
            ReferenceCountUtil.release(rpcRequest);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("新的channel已创建");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
                logger.info("100秒内没有数据传输,自动断开连接...");
                ctx.close();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("过程调用时有错误发生:");
        if(cause instanceof IOException) {
            logger.error("远程主机强迫关闭了一个现有的连接");
            ctx.close();
        } else {
            cause.printStackTrace();
            ctx.close();
        }
    }


}
