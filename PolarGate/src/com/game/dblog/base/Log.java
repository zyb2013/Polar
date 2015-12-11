package com.game.dblog.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 日志字段注解
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Log {
	/**
	 * 字段名
	 * @return
	 */
	public String logField();
	/**
	 * 字段类型
	 * @return
	 */
	public String fieldType();
}
