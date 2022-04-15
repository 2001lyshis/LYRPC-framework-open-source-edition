package rpc.core.common.annotation;

import rpc.core.common.context.RpcContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcFilter {
    String filterServiceName() default RpcContext.DEFAULT_FILTER_NAME;
    int nice() default 0;
}
