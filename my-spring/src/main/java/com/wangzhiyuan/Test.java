package com.wangzhiyuan;

import com.spring.ApplicationContext;
import com.spring.BeanNameAutoProxyCreator;
import com.wangzhiyuan.Appconfig;
import com.wangzhiyuan.service.UserService;

/**
 * @author wzy
 * @version 1.0
 * @description: 测试类
 * @date 2022/1/5 9:24
 */
public class Test {

	public static void main(String[] args) {
		ApplicationContext applicationContext = new ApplicationContext(Appconfig.class);
		BeanNameAutoProxyCreator beanNameAutoProxyCreator = new BeanNameAutoProxyCreator("orderService", "testMethodInterceptor");
		applicationContext.registerSingletonBean("beanNameAutoProxyCreator",beanNameAutoProxyCreator);
		UserService userService = applicationContext.getBean("userService", UserService.class);
		System.out.println(userService);
		userService.test();
	}
}
