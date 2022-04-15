package rpc.core.transport.netty.server;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.core.common.annotation.RpcFilter;
import rpc.core.common.annotation.RpcInterceptor;
import rpc.core.common.annotation.RpcService;
import rpc.core.common.annotation.RpcServiceScan;
import rpc.core.common.entity.RpcContext;
import rpc.core.common.enumeration.RpcError;
import rpc.core.common.excption.RpcException;
import rpc.core.common.factory.SingletonFactory;
import rpc.core.common.util.ClassUtil;
import rpc.core.provider.ServiceProvider;
import rpc.core.registry.ServiceRegistry;
import rpc.core.transport.RpcServer;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.*;

public abstract class AbstractRpcServer implements RpcServer {

    protected Logger logger = LoggerFactory.getLogger(AbstractRpcServer.class);

    protected String host;
    protected int port;

    protected ServiceProvider serviceProvider;
    protected ServiceRegistry serviceRegistry;

    private static Map<String, List<rpc.core.filter.RpcFilter>> filterMap = RpcContext.filterMap;
    private static Map<String, List<rpc.core.intercepter.RpcInterceptor>> interceptorMap = RpcContext.interceptorMap;

    private static Map<String, List<Map.Entry>> entryFilterMap = RpcContext.entryFilterMap;
    private static Map<String, List<Map.Entry>> entryInterceptorMap = RpcContext.entryInterceptorMap;


    @Override
    public <T> void publishService(Method service, String serviceName) {
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
        serviceProvider.addService(service, serviceName);
    }

    @Override
    public void deleteService(InetSocketAddress socketAddress, String serviceName) {
        serviceRegistry.remove(serviceName, socketAddress);
        serviceProvider.removeService(serviceName);
    }

    @Override
    public abstract void shutdown();

    protected void scan(){
        scanService();
    }

    @SuppressWarnings("unchecked")
    private void scanService() {
        String mainClassName = ClassUtil.getMainClassName();
        RpcContext.MainClassName = mainClassName;
        Class<?> startClass = checkAnnotationAndGetStartClass(mainClassName);
        String basePackage = startClass.getAnnotation(RpcServiceScan.class).value();
        if("".equals(basePackage)) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }


        List<Class<?>> classList = ClassUtil.searchClasses(basePackage, true);
        for(Class<?> clazz : classList) {
            logger.info("Server正在扫描class文件 {}", clazz.getName());
            // 将标注有@RpcSevice的服务进行注册
            // 如果是标注在类上的RpcService注解
            if(clazz.isAnnotationPresent(RpcService.class)) {
                String serviceName = clazz.getInterfaces()[0].getSimpleName();
                try {
                    SingletonFactory.getInstance(clazz);
                } catch (Exception e) {
                    logger.error("实例化 " + clazz + "时发生错误");
                    continue;
                }
                Method[] methods = clazz.getDeclaredMethods();
                for(Method method : methods) {
                    publishService(method, serviceName + "@" + method.getName());
                }
            } else if (!RpcContext.lazyInit && clazz.isAnnotationPresent(RpcInterceptor.class)) {
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
            } else if (!RpcContext.lazyInit &&clazz.isAnnotationPresent(RpcFilter.class)) {
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
            else { // 只是标注在方法上的注解
                Method[] methods = clazz.getDeclaredMethods();
                for(Method method : methods) {
                    if(method.getAnnotation(RpcService.class) != null) {
                        String serviceName = clazz.getInterfaces()[0].getSimpleName();
                        try {
                              SingletonFactory.getInstance(clazz);
                        } catch (Exception e) {
                            logger.error("实例化 " + clazz + "时发生错误");
                            continue;
                        }
                        publishService(method, serviceName + "@" + method.getName());
                    }
                }
            }
        }
        scanSuccess();
    }

    private Class<?> checkAnnotationAndGetStartClass(String startClassName) {
        try {
            Class<?> startClass = Class.forName(startClassName);
            if(!startClass.isAnnotationPresent(RpcServiceScan.class)) {
                logger.error("启动类缺少 @ServiceScan 注解");
                throw new RpcException(RpcError.START_REQUEST_ANNOTATION);
            }
            return startClass;
        } catch (ClassNotFoundException e) {
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
    }

    private void scanSuccess(){
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
