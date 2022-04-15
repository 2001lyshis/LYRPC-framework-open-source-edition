package rpc.core.transport.netty.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.core.common.entity.RpcRequest;
import rpc.core.common.entity.RpcResponse;
import rpc.core.common.enumeration.RpcError;
import rpc.core.common.factory.RpcRequestFactory;
import rpc.core.common.util.RpcMessageChecker;
import rpc.core.transport.RpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RpcClientProxy<T> implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);

    private RpcClient client;

    public RpcClientProxy(RpcClient client) {
        this.client = client;
    }


    public Object getProxy(Class clazz) {
        return  Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        /**
         *  以下equals/hashCode/toString 非常重要，不能删除，
         *  否则在debug时，会调到Object.toString方法从而执行调用远程扩展点
         *  在debug时，调到此方法，但无法断点住，应该是idea工具上为了显示对象调用的
         */
        if ("equals".equals(method.getName())) {
            try {
                Object otherHandler =
                        args.length > 0 && args[0] != null ? Proxy.getInvocationHandler(args[0]) : null;
                return equals(otherHandler);
            } catch (IllegalArgumentException e) {
                return false;
            }
        } else if ("hashCode".equals(method.getName())) {
            return hashCode();
        } else if ("toString".equals(method.getName())) {
            return toString();
        } else {
            // logger.info("调用方法: {}@{}", method.getDeclaringClass().getSimpleName(), method.getName());
            RpcRequest request = RpcRequestFactory.createRpcRequest(method.getDeclaringClass().getSimpleName(), method.getName(), args, method.getParameterTypes());
            RpcResponse response;
            try {
                CompletableFuture<RpcResponse> completableFuture = ((CompletableFuture<RpcResponse>) client.sendRequest(request));
                if(completableFuture == null) {
                    return null;
                }
                response = completableFuture.get(10, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                logger.error(RpcError.REQUEST_TIMEOUT.getMessage());
                return null;
            } catch (Exception e) {
                logger.error("方法调用失败", e);
                return null;
            }
            RpcMessageChecker.receiveCheck(request, response);
            return response.getData();
        }
    }

}
