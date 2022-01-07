package com.spring;

/**
 * @author wzy
 * @version 1.0
 * @description: TODO
 * @date 2022/1/7 15:55
 */
public class BeanNameAutoProxyCreator implements InitializingBean{

	private String beanName;
	private String interceptorName;

	public BeanNameAutoProxyCreator() {
	}

	public BeanNameAutoProxyCreator(String beanName, String interceptorName) {
		this.beanName = beanName;
		this.interceptorName = interceptorName;
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public String getInterceptorName() {
		return interceptorName;
	}

	public void setInterceptorName(String interceptorName) {
		this.interceptorName = interceptorName;
	}

	@Override
	public void afterPropertiesSet() {

	}
}
