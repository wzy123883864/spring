package com.spring;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wzy
 * @version 1.0
 * @description: bean定义
 * @date 2022/1/5 14:27
 */
public class BeanDefinition {

	private Class clazz;
	private String scope = "singleton";

	public BeanDefinition() {
	}

	public BeanDefinition(Class clazz, String scope) {
		this.clazz = clazz;
		this.scope = scope;
	}

	public Class getClazz() {
		return clazz;
	}

	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

}
