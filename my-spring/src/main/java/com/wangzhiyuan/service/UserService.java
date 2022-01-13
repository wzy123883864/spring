package com.wangzhiyuan.service;

import com.spring.*;

/**
 * @author wzy
 * @version 1.0
 * @date 2022/1/5 9:30
 */
@Component
public class UserService implements InitializingBean {


	private OrderService orderService;

	private String beanName;

	public UserService(){
		System.out.println("我是无参构造器");
	};
	@Autowired
	public UserService(OrderService orderService) {
		this.orderService = orderService;
	}

	public void test(){
		System.out.println("userservice中的orderservice = " + orderService);
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	@Override
	public void afterPropertiesSet() {

	}
}
