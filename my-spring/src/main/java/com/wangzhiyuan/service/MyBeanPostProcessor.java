package com.wangzhiyuan.service;

import com.spring.BeanPostProcessor;
import com.spring.Component;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author wzy
 * @version 1.0
 * @description: 模拟BeanPostProcessor
 * @date 2022/1/6 13:40
 */
//@Component
public class MyBeanPostProcessor implements BeanPostProcessor {


	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName){
		if("userService".equals(beanName)){
			((UserService)bean).setBeanName("WZY");
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName){


		Enhancer enhancer = new Enhancer();
		enhancer.setUseCache(true);
		enhancer.setSuperclass(bean.getClass());
		enhancer.setClassLoader(bean.getClass().getClassLoader());
		enhancer.setCallback(new MethodInterceptor() {
			@Override
			public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
				System.out.println("Aop前逻辑");
				System.out.println("开始时间：" + System.currentTimeMillis());
				Object invoke = method.invoke(bean,objects);
				System.out.println("Aop后逻辑");
				System.out.println("开始时间：" + System.currentTimeMillis());
				return invoke;
			}
		});
		return enhancer.create();

	}
}
