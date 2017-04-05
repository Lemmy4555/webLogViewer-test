package com.sc.l45.weblogviewer.test.utils;

import java.util.List;
import java.util.OptionalDouble;

public class MathUtils {
    public static OptionalDouble avarage(List<Long> timeList) {
        return timeList
        .stream()
        .mapToLong(a -> a)
        .average();
    }
}
