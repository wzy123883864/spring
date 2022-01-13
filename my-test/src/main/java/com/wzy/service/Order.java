package com.wzy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wzy
 * @version 1.0
 * @description: TODO
 * @date 2022/1/7 8:35
 */
@Component
public class Order {

	@Autowired
	User user;

	public void test(){
		System.out.println("order中user的值为"+user);
	}
}
