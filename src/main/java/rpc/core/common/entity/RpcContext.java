package rpc.core.common.entity;


import rpc.core.filter.RpcFilter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcContext {
    // Nacos地址
    public static final String NACOS_SERVER_ADD = "127.0.0.1:8848";
    public static String MainClassName = "";

    // filter 与 interceptor的懒加载机制: 是否交给Handler加载
    public static boolean lazyInit = true;

    public static Map<String, List<RpcFilter>> filterMap = new ConcurrentHashMap<>();
    public static Map<String, List<rpc.core.intercepter.RpcInterceptor>> interceptorMap = new ConcurrentHashMap<>();

    public static Map<String, List<Map.Entry>> entryInterceptorMap = new ConcurrentHashMap<>();
    public static Map<String, List<Map.Entry>> entryFilterMap = new ConcurrentHashMap<>();

}
