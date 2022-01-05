package com.test;

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
}
