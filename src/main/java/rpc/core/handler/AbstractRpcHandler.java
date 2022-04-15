package rpc.core.handler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.core.common.annotation.RpcFilter;
import rpc.core.common.annotation.RpcInterceptor;
import rpc.core.common.entity.RpcContext;
import rpc.core.common.entity.RpcRequest;
import rpc.core.common.entity.RpcResponse;
import rpc.core.common.factory.SingletonFactory;
import rpc.core.common.util.ClassUtil;

import rpc.core.filter.DefaultFilterChain;
import rpc.core.filter.TailFilter;



import java.io.IOException;
import java.util.*;

abstract class AbstractRpcHandler {
    private static final Logger logger = LoggerFactory.getLogger(AbstractRpcHandler.class);
    private static Map<String, List<rpc.core.filter.RpcFilter>> filterMap = RpcContext.filterMap;
    private static Map<String, List<rpc.core.intercepter.RpcInterceptor>> interceptorMap = RpcContext.interceptorMap;

    private static Map<String, List<Map.Entry>> entryFilterMap = RpcContext.entryFilterMap;
    private static Map<String, List<Map.Entry>> entryInterceptorMap = RpcContext.entryInterceptorMap;

     void scan() {
        scanHandler();
    }

    @SuppressWarnings("unchecked")
    private void scanHandler() {

        String mainClassName = RpcContext.MainClassName;
        String basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));

        List<Class<?>> classList = ClassUtil.searchClasses(basePackage, true);
        for (Class<?> clazz : classList) {
            if (clazz.isAnnotationPresent(RpcInterceptor.class)) {
                String InterceptorServiceName = clazz.getAnnotation(RpcInterceptor.class).interceptorServiceName();
                int nice = clazz.getAnnotation(RpcInterceptor.class).nice();
                List<Map.Entry> entries;
                Object obj;
                try {
                    obj = SingletonFactory.getInstance(clazz);
                } catch (Exception e) {
                    continue;
                }
                if (entryInterceptorMap.containsKey(InterceptorServiceName)) {
                    entries = entryInterceptorMap.get(InterceptorServiceName);
                } else {
                    entries = new ArrayList<>();
                }

                Map.Entry entry = new AbstractMap.SimpleEntry(nice, obj);
                entries.add(entry);
                entryInterceptorMap.put(InterceptorServiceName, entries);
            } else if (clazz.isAnnotationPresent(RpcFilter.class)) {
                String FilterServiceName = clazz.getAnnotation(RpcFilter.class).filterServiceName();
                int nice = clazz.getAnnotation(RpcFilter.class).nice();
                List<Map.Entry> entries;
                Object obj;
                try {
                    obj = SingletonFactory.getInstance(clazz);
                } catch (Exception e) {
                    continue;
                }
                if (entryFilterMap.containsKey(FilterServiceName)) {
                    entries = entryFilterMap.get(FilterServiceName);
                } else {
                    entries = new ArrayList<>();
                }

                Map.Entry entry = new AbstractMap.SimpleEntry(nice, obj);
                entries.add(entry);
                entryFilterMap.put(FilterServiceName, entries);
            }
        }
        scanSuccess();
    }


    // 过滤器
    void doFilter(RpcRequest request, RpcResponse response, String filterServiceName) throws IOException {

        List<rpc.core.filter.RpcFilter> filters = getSortedFilters(filterServiceName);
        doFilter(filters, request, response);
        Collections.reverse(filters);
    }

    private void doFilter(List<rpc.core.filter.RpcFilter> filters, RpcRequest rpcRequest, RpcResponse response) throws IOException {

        DefaultFilterChain filterChain = new DefaultFilterChain();
        rpc.core.filter.RpcFilter head = new TailFilter();
        filterChain.addFilter(head);
        for (rpc.core.filter.RpcFilter filter : filters) {
            filterChain.addFilter(filter);
        }
        head.doFilter(rpcRequest, response, filterChain);
    }

    private List<rpc.core.filter.RpcFilter> getSortedFilters(String filterServiceName) {

        if (filterServiceName == null || "".equals(filterServiceName) || entryFilterMap.get(filterServiceName) == null)
            filterServiceName = "RPC_Filter";

        if (filterMap.containsKey(filterServiceName))
            return filterMap.get(filterServiceName);

        List<rpc.core.filter.RpcFilter> ret = new ArrayList<>();
        List<Map.Entry> filters = entryFilterMap.get("RPC_Filter");

        if(filters != null) {
            for (Map.Entry entry : filters) {
                ret.add((rpc.core.filter.RpcFilter) entry.getValue());
            }
        }
        filterMap.put(filterServiceName, ret);
        return ret;
    }


    // 拦截器
    boolean doIntercept(RpcRequest request, RpcResponse response, String interceptorServiceName) throws Exception {
        List<rpc.core.intercepter.RpcInterceptor> interceptors = getSortedInterceptors(interceptorServiceName);
        for (rpc.core.intercepter.RpcInterceptor interceptor : interceptors) {
            if (!interceptor.preHandle(request, response))
                return false;
        }
        return true;
    }

    private List<rpc.core.intercepter.RpcInterceptor> getSortedInterceptors(String interceptorServiceName) {

        if(interceptorServiceName == null || "".equals(interceptorServiceName) || entryInterceptorMap.get(interceptorServiceName) == null)
            interceptorServiceName = "RPC_Interceptor";

        if(interceptorMap.containsKey(interceptorServiceName))
            return interceptorMap.get(interceptorServiceName);

        List<rpc.core.intercepter.RpcInterceptor> ret = new ArrayList<>();
        List<Map.Entry> filters = entryInterceptorMap.get("RPC_Interceptor");

        if(filters != null) {
            for (Map.Entry entry : filters) {
                ret.add((rpc.core.intercepter.RpcInterceptor) entry.getValue());
            }
        }
        interceptorMap.put(interceptorServiceName, ret);
        return ret;
    }

    void scanSuccess(){
        for (List<Map.Entry> list : entryFilterMap.values()) {
            list.sort(Comparator.comparingInt(entry -> (int) entry.getKey()));
        }
        for (List<Map.Entry> list : entryInterceptorMap.values()) {
            list.sort(Comparator.comparingInt(entry -> (int) entry.getKey()));
        }
        RpcContext.entryFilterMap = entryFilterMap;
        RpcContext.entryInterceptorMap = entryInterceptorMap;
        RpcContext.filterMap = filterMap;
        RpcContext.interceptorMap = interceptorMap;
    }
}

