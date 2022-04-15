package rpc.core.handler;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.core.common.context.HandlerContext;
import rpc.core.common.context.RpcContext;
import rpc.core.common.entity.RpcRequest;
import rpc.core.common.entity.RpcResponse;
import rpc.core.common.enumeration.ResponseCode;
import rpc.core.common.factory.RpcResponseFactory;
import rpc.core.common.factory.SingletonFactory;
import rpc.core.provider.ServiceProvider;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;



/**
 *  任何接受request请求必须进过该handler处理，使之能够调用相应的本地方法
 *  加一点过滤器和拦截器吧！
 *
 *  2022/4/12
 */
public class RpcSeverHandler extends AbstractRpcHandler{

    private static final Logger logger = LoggerFactory.getLogger(RpcSeverHandler.class);
    private static HandlerContext context;
    private static ServiceProvider serviceProvider;

    public RpcSeverHandler(HandlerContext cxt) {
        context = cxt;

    }



    public RpcResponse handle(RpcRequest rpcRequest) throws Exception {
        RpcResponse initResponse = RpcResponseFactory.createResponse(rpcRequest.getRequestId());

        // 过滤器和拦截器
        doFilter(rpcRequest, initResponse, rpcRequest.getInterfaceName());
        if(!doIntercept(rpcRequest, initResponse, context)) {
            return null;
        }
        serviceProvider = context.getProvider();
        if(serviceProvider == null) {
            serviceProvider = RpcContext.ProviderGroups.get(RpcContext.DEFAULT_PROVIDER_NAME);
        }
        Method service = serviceProvider.getService( rpcRequest.getInterfaceName() + "@" + rpcRequest.getMethodName());

        Object res = invokeTargetMethod(rpcRequest, service);

        RpcResponse response = RpcResponseFactory.addResult(initResponse, res);
        doFilter(rpcRequest, initResponse, rpcRequest.getInterfaceName());

        return response;
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Method method) {
        Object result;
        try {
            Object service = SingletonFactory.getInstance(method.getDeclaringClass());
            // 本地调用的核心
            result = method.invoke(service, rpcRequest.getParameters());
            // logger.info("服务:{} 成功调用方法:{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());

        } catch (IllegalAccessException | InvocationTargetException e) {
            return RpcResponseFactory.fail(ResponseCode.METHOD_NOT_FOUND.getMessage(), rpcRequest.getRequestId());
        }
        return result;
    }
}
