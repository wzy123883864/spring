package com.wzy;

import com.wzy.service.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author wzy
 * @version 1.0
 * @description: TODO
 * @date 2021/12/14 9:48
 */
public class MyApplication {

	public static void main(String[] args) {

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.wzy");
		User user = (User)context.getBean("user");
		user.test();

	}
}
