package rpc.core.transport;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.core.common.annotation.RpcFilter;
import rpc.core.common.annotation.RpcInterceptor;
import rpc.core.common.annotation.RpcService;
import rpc.core.common.annotation.RpcServiceScan;
import rpc.core.common.context.RpcContext;
import rpc.core.common.enumeration.RpcError;
import rpc.core.common.excption.RpcException;
import rpc.core.common.factory.SingletonFactory;
import rpc.core.common.util.ClassUtil;
import rpc.core.common.util.TimeUtil;
import rpc.core.provider.ServiceProvider;
import rpc.core.provider.ServiceProviderImpl;
import rpc.core.registry.ServiceRegistry;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.*;

public abstract class AbstractRpcServer implements RpcServer {

    protected Logger logger = LoggerFactory.getLogger(AbstractRpcServer.class);

    protected static String host;
    protected static int port;

    protected ServiceProvider serviceProvider;
    protected ServiceRegistry serviceRegistry;

    private static Map<String, List<rpc.core.filter.RpcFilter>> filterMap = RpcContext.filterMap;
    private static Map<String, List<rpc.core.intercepter.RpcInterceptor>> interceptorMap = RpcContext.interceptorMap;

    private static Map<String, List<Map.Entry>> entryFilterMap = RpcContext.entryFilterMap;
    private static Map<String, List<Map.Entry>> entryInterceptorMap = RpcContext.entryInterceptorMap;

    protected AbstractRpcServer(){
        TimeUtil.timerStart();
    }

    @Override
    public <T> void publishService(Method service, String serviceName, ServiceProvider provider) {
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
        provider.addService(service, serviceName);
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
            try {
                basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
            } catch (NullPointerException e) {
                basePackage = mainClassName;
            }
        }

        List<Class<?>> classList = ClassUtil.searchClasses(basePackage, true);
        for(Class<?> clazz : classList) {
            // 查看标注在方法上的注解
            if(clazz.isAnnotationPresent(RpcService.class)) {
                try {
                    getClassService(clazz);
                } catch (Exception e) {
                    logger.warn("实例化 " + clazz.getName() + "时发生错误");
                }
            } else if (clazz.isAnnotationPresent(RpcInterceptor.class)) {
                try {
                    getInterceptor(clazz);
                } catch (Exception e) {
                    logger.warn("实例化 " + clazz.getName() + "时发生错误");
                }
            } else if (clazz.isAnnotationPresent(RpcFilter.class)) {
                try {
                    getFilter(clazz);
                } catch (Exception e) {
                    logger.warn("实例化 " + clazz.getName() + "时发生错误");
                }
            } else {
               try {
                   getMethodService(clazz);
               } catch (Exception e) {
                   logger.warn("实例化 " + clazz.getName() + "时发生错误");
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

    @SuppressWarnings("unchecked")
    private void getInterceptor(Class<?> clazz) throws Exception {
        Object obj = SingletonFactory.getInstance(clazz);
        String InterceptorServiceName = clazz.getAnnotation(RpcInterceptor.class).interceptorServiceName();
        InterceptorServiceName = InterceptorServiceName.equals("") ? RpcContext.DEFAULT_INTERCEPTOR_NAME : InterceptorServiceName;
        int nice = clazz.getAnnotation(RpcInterceptor.class).nice();

        List<Map.Entry> entries = entryInterceptorMap.getOrDefault(InterceptorServiceName, new ArrayList<>());
        Map.Entry entry = new AbstractMap.SimpleEntry(nice, obj);
        entries.add(entry);
        entryInterceptorMap.put(InterceptorServiceName, entries);
    }

    @SuppressWarnings("unchecked")
    private void getFilter(Class<?> clazz) throws Exception {
        Object obj = SingletonFactory.getInstance(clazz);
        String FilterServiceName = clazz.getAnnotation(RpcFilter.class).filterServiceName();
        FilterServiceName = FilterServiceName.equals("") ? RpcContext.DEFAULT_FILTER_NAME : FilterServiceName;
        int nice = clazz.getAnnotation(RpcFilter.class).nice();

        List<Map.Entry> entries = entryFilterMap.getOrDefault(FilterServiceName, new ArrayList<>());
        Map.Entry entry = new AbstractMap.SimpleEntry(nice, obj);
        entries.add(entry);
        entryFilterMap.put(FilterServiceName, entries);
    }

    private void getClassService(Class<?> clazz) throws Exception {
        String serviceName = clazz.getInterfaces()[0].getSimpleName();
        SingletonFactory.getInstance(clazz);
        Method[] methods = clazz.getDeclaredMethods();
        String providerName = clazz.getAnnotation(RpcService.class).providerName();
        providerName = providerName.equals("") ? RpcContext.DEFAULT_PROVIDER_NAME : providerName;
        ServiceProvider provider = RpcContext.ProviderGroups.getOrDefault(providerName, new ServiceProviderImpl());
        for(Method method : methods) {
            publishService(method, serviceName + "@" + method.getName(), provider);
        }
        RpcContext.ProviderGroups.put(providerName, provider);
    }

    private void getMethodService(Class<?> clazz) throws Exception {
        Method[] methods = clazz.getDeclaredMethods();
        for(Method method : methods) {
            if(method.getAnnotation(RpcService.class) != null) {
                String serviceName = clazz.getInterfaces()[0].getSimpleName();
                SingletonFactory.getInstance(clazz);
                String providerName = method.getAnnotation(RpcService.class).providerName();
                providerName = providerName.equals("") ? RpcContext.DEFAULT_PROVIDER_NAME : providerName;
                ServiceProvider provider = RpcContext.ProviderGroups.getOrDefault(providerName, new ServiceProviderImpl());
                publishService(method, serviceName + "@" + method.getName(), provider);
                RpcContext.ProviderGroups.putIfAbsent(providerName, provider);
            }
        }
    }
}
