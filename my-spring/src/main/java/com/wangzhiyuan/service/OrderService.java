package com.wangzhiyuan.service;

import com.spring.Autowired;
import com.spring.Component;
import com.spring.Scope;

/**
 * @author wzy
 * @version 1.0
 * @date 2022/1/5 9:30
 */
@Component
public class OrderService {

	@Autowired
	private UserService userService;

	public void test(){

		System.out.println("OrderService的userservice="+userService);
	}
}
