package com.game.utils;

import java.util.Collection;
import java.util.Map;

/**
 * @author luminghua
 *
 * @date   2014年2月13日 上午10:48:45
 */
public class CollectionUtil {

	@SuppressWarnings("rawtypes")
	public static boolean isBlank(Collection c) {
		return c == null || c.size() == 0;
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean isNotBlank(Collection c) {
		return c != null && c.size() > 0;
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean isBlank(Map m) {
		return m == null || m.size() == 0;
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean isNotBlank(Map m) {
		return m != null && m.size() > 0;
	}
	
	public static boolean isBlank(Object[] a) {
		return a == null || a.length == 0;
	}
	
	public static boolean isNotBlank(Object[] a) {
		return a != null && a.length > 0;
	}
}
