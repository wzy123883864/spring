package com.spring;

@FunctionalInterface
public interface ObjectFactory<T> {

	T getObject();
}
