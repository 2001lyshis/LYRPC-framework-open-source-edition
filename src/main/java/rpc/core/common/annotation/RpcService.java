package rpc.core.common.annotation;

import rpc.core.common.context.RpcContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  如果标注在类上,则本类的所有方法将会被注册为服务
 *  如果标注在方法上,则只有该方法会被注册为服务
 */

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {
    String providerName() default RpcContext.DEFAULT_PROVIDER_NAME;
}
