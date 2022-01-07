package com.spring;

import java.util.Map;

public interface BeanAware {

	void setBeanName(String beanName);

	void setBeanFactory(Map<String,Object> beanFactory);
}
