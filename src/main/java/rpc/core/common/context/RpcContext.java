package rpc.core.common.context;


import rpc.core.filter.RpcFilter;
import rpc.core.provider.ServiceProvider;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcContext {

    public static final String NACOS_SERVER_ADD = "127.0.0.1:8848";     // Nacos地址
    public static final String DEFAULT_PROVIDER_NAME = "DefaultProvider";
    public static final String DEFAULT_INTERCEPTOR_NAME = "RPC_Interceptor";
    public static final String DEFAULT_FILTER_NAME = "RPC_Filter";

    public static String MainClassName = "";

    public static Map<String, ServiceProvider> ProviderGroups = new ConcurrentHashMap<>();    // 不同的服务表
    public static Map<String, List<RpcFilter>> filterMap = new ConcurrentHashMap<>();
    public static Map<String, List<rpc.core.intercepter.RpcInterceptor>> interceptorMap = new ConcurrentHashMap<>();
    public static Map<String, List<Map.Entry>> entryInterceptorMap = new ConcurrentHashMap<>();
    public static Map<String, List<Map.Entry>> entryFilterMap = new ConcurrentHashMap<>();

    public static final long ServerIdealStateTime = 100 ;  // 秒
    public static final long ClientRequestTimeOut = 10; // 秒

}
