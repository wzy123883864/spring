package com.wangzhiyuan.service;

import com.spring.BeanPostProcessor;
import com.spring.Component;

/**
 * @author wzy
 * @version 1.0
 * @description: 模拟BeanPostProcessor
 * @date 2022/1/6 13:40
 */
@Component
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


		return bean;

	}
}
