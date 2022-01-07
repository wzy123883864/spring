package com.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author wzy
 * @version 1.0
 * @description: TODO
 * @date 2022/1/6 17:18
 */
public class ProxyBean implements InvocationHandler {

	private Object target;

	public ProxyBean(Object target){
		this.target = target;
	}

	public Object getTarget() {
		return target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("Aop前逻辑");
		System.out.println("开始时间：" + System.currentTimeMillis());
		Object invoke = method.invoke(proxy, args);
		System.out.println("Aop后逻辑");
		System.out.println("开始时间：" + System.currentTimeMillis());
		return invoke;
	}
}
