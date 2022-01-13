package com.wangzhiyuan;

import com.spring.ApplicationContext;
import com.wangzhiyuan.service.OrderService;
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
		UserService userService = applicationContext.getBean("userService", UserService.class);
		OrderService orderService = applicationContext.getBean("orderService", OrderService.class);
		userService.test();
		orderService.test();
	}
}
