package rpc.core.handler;

import io.netty.util.ReferenceCountUtil;;
import rpc.core.common.entity.RpcResponse;
import rpc.core.common.factory.SingletonFactory;
import rpc.core.provider.UnprocessedRequestProvider;

public class RpcClientHandler extends AbstractRpcHandler {

    private final UnprocessedRequestProvider unprocessedRequests;



    public RpcClientHandler() {
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequestProvider.class);
    }

    public void handle(RpcResponse response){
//        doFilter(request, response);
        try {
            unprocessedRequests.complete(response);
        } finally {
            ReferenceCountUtil.release(response);
        }
//        doFilter(request, response);
    }

}
