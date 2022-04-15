package rpc.core.transport.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import rpc.core.common.context.RpcContext;
import rpc.core.common.factory.SingletonFactory;
import rpc.core.common.serializer.CommonSerializer;
import rpc.core.common.util.TimeUtil;
import rpc.core.transport.AbstractRpcServer;
import rpc.core.transport.netty.handler.NettyServerHandler;
import rpc.core.provider.ServiceProviderImpl;
import rpc.core.registry.Impl.NacosServiceRegistry;
import rpc.core.transport.netty.handler.CommonDecode;
import rpc.core.transport.netty.handler.CommonEncode;

import java.util.concurrent.TimeUnit;

public class NettyServer extends AbstractRpcServer {

    private final CommonSerializer serializer;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public NettyServer(String host, int port) {
        this(host, port, CommonSerializer.DEFAULT_SERIALIZER);
    }
    public NettyServer(String host, int port, Integer serializer) {
        this.host = host;
        this.port = port;
        serviceRegistry = SingletonFactory.getInstance(NacosServiceRegistry.class);
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
        this.serializer = CommonSerializer.getByCode(serializer);
        scan();
    }


    @Override
    public void run() {
        new Thread(() -> {
            try {
                start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    @Override
    public void start() {

         this.bossGroup = new NioEventLoopGroup();
         this.workerGroup = new NioEventLoopGroup();
        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new IdleStateHandler(RpcContext.ServerIdealStateTime, RpcContext.ServerIdealStateTime, RpcContext.ServerIdealStateTime, TimeUnit.SECONDS))
                                    .addLast(new CommonEncode(serializer))
                                    .addLast(new CommonDecode())
                                    .addLast(new NettyServerHandler());
                        }
                    });

            TimeUtil.serverTimerStop();
            ChannelFuture future = serverBootstrap.bind(host, port).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("启动时发生错误 : ", e);
        } finally {
           shutdown();
        }
    }

    @Override
    public void shutdown() {
        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }


}
