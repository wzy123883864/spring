package com.wzy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author wzy
 * @version 1.0
 * @description: TODO
 * @date 2022/1/7 9:34
 */
@Configuration
public class AppConfig {

	@Bean
	public MethodInterceptor testMethodInterceptor(){
		return new MethodInterceptor() {
			@Nullable
			@Override
			public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
				System.out.println("前置代理逻辑");
				Object proceed = invocation.proceed();
				System.out.println("后置代理逻辑");
				return proceed;
			}
		};
	}

	@Bean
	public BeanNameAutoProxyCreator beanNameAutoProxyCreator(){
		BeanNameAutoProxyCreator beanNameAutoProxyCreator = new BeanNameAutoProxyCreator();
		beanNameAutoProxyCreator.setBeanNames("order");
		beanNameAutoProxyCreator.setInterceptorNames("testMethodInterceptor");
		beanNameAutoProxyCreator.setProxyTargetClass(true);
		return beanNameAutoProxyCreator;
	}
}
