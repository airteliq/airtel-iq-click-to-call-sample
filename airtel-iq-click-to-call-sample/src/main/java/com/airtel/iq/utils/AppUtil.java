package com.airtel.iq.utils;

import java.util.Collection;
import java.util.Map;

public class AppUtil {
	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	public static boolean isEmpty(Map<?, ?> map) {
		return map == null || map.isEmpty();
	}

	public static boolean isEmpty(Object object) {
		return object == null;
	}

	public static boolean isEmpty(Object[] array) {
		return array == null || array.length == 0;
	}

	public static boolean isEmpty(String string) {
		return string == null || string.trim().length() == 0;
	}

	public static boolean isNotNullOrEmpty(String str) {
		return str != null && !str.isEmpty();
	}

	public static String getValueOrDefault(String value, String defaultValue) {
		return isNotNullOrEmpty(value) ? value : defaultValue;
	}
}
