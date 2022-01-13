package com.wangzhiyuan;

import com.spring.Bean;
import com.spring.BeanNameAutoProxyCreator;
import com.spring.ComponentScan;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author wzy
 * @version 1.0
 * @description: TODO
 * @date 2022/1/5 9:25
 */
@ComponentScan("com.wangzhiyuan.service")
public class Appconfig {

	@Bean
	public BeanNameAutoProxyCreator beanNameAutoProxyCreator(){
		return  new BeanNameAutoProxyCreator("orderService", "testMethodInterceptor");
	}

	@Bean
	public MethodInterceptor testMethodInterceptor(){
		return new MethodInterceptor() {
			@Override
			public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
				System.out.println("代理前置逻辑");
				Object invoke = methodProxy.invokeSuper(o,objects);
				System.out.println("代理后置逻辑");
				return invoke;
			}
		};
	}
}
