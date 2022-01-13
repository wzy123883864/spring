package com.wzy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wzy
 * @version 1.0
 * @date 2021/12/14 9:47
 */
@Component
public class User {

	private int id = 1;
	private String name;

	private Order order;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Autowired
	public User(Order order) {
		this.order = order;
	}

	public void test(){

		System.out.println("user中order的值为 ："+ order);
		order.test();
	}
}
