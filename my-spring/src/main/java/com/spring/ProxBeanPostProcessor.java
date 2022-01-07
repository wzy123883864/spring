package com.spring;

import java.util.Map;

/**
 * @author wzy
 * @version 1.0
 * @description: TODO
 * @date 2022/1/7 15:53
 */
public class ProxBeanPostProcessor implements BeanPostProcessor,BeanAware{

	private Map<String,Object> beanFactory;
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		if(bean instanceof BeanNameAutoProxyCreator){
			BeanNameAutoProxyCreator banNameAutoProxyCreator = (BeanNameAutoProxyCreator)bean;
			String proxybeanName = banNameAutoProxyCreator.getBeanName();
			String interceptorName = banNameAutoProxyCreator.getInterceptorName();
			Object o = beanFactory.get(proxybeanName);
			System.out.println(o);
		}
		return bean;
	}

	@Override
	public void setBeanName(String beanName) {

	}

	@Override
	public void setBeanFactory(Map<String, Object> beanFactory) {
		this.beanFactory = beanFactory;
	}
}
