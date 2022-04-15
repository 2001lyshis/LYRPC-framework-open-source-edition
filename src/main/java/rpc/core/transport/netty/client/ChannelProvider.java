package rpc.core.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import rpc.core.common.serializer.CommonSerializer;
import rpc.core.transport.netty.handler.NettyClientHandler;
import rpc.core.transport.netty.handler.CommonDecode;
import rpc.core.transport.netty.handler.CommonEncode;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class ChannelProvider {

    private static EventExecutorGroup bizGroup = new DefaultEventExecutorGroup(10);
    private static Bootstrap bootstrap = initializeBootstrap();
    private static Map<String, Channel> channels = new ConcurrentHashMap<>();

    private static Bootstrap initializeBootstrap() {

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                //连接的超时时间，超过这个时间还是建立不上的话则代表连接失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        return bootstrap;
    }

    public static Channel get(InetSocketAddress inetSocketAddress, CommonSerializer serializer) throws InterruptedException {
        String key = inetSocketAddress.toString() + inetSocketAddress.getPort() + serializer.getCode() ;
        if (channels.containsKey(key)) {
            Channel channel = channels.get(key);
            if(channels != null && channel.isActive()) {
                return channel;
            } else {
                channels.remove(key);
            }
        }
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                /*自定义序列化编解码器*/
                // RpcResponse -> ByteBuf
                ch.pipeline()
                        // 设置心跳 writerIdleTime 时间
                        .addLast(new CommonDecode())
                        .addLast(bizGroup, new NettyClientHandler())
                        .addLast(new CommonEncode(serializer));
            }
        });
        Channel channel;
        try {
            channel = connect(bootstrap, inetSocketAddress);
        } catch (ExecutionException e) {
            return null;
        }
        channels.put(key, channel);
        return channel;
    }


    private static Channel connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
                completableFuture.complete(future.channel());
        });
        return completableFuture.get();
    }
}
