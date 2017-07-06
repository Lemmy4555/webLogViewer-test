package com.sc.l45.weblogviewer.api.utils;

import java.util.List;

public class ListUtils {
	public static String getLastLine(List<String> list) {
		return list.get(list.size() - 1);
	}
	
	public static String getFirstLine(List<String> list) {
		return list.get(0);
	}

	public static void removeFirstLine(List<String> list) {
		list.remove(0);
	}
	
	public static void removeLastLine(List<String> list) {
		list.remove(list.size() - 1);
	}

	public static void setLastLine(List<String> list, String toSet) {
		list.set(list.size() - 1, toSet);
	}

	public static void setFirstLine(List<String> list, String toSet) {
		list.set(0, toSet);
	}
}
