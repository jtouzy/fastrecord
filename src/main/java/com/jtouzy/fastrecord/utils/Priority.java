package com.jtouzy.fastrecord.utils;

import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public final class Priority {
    public static final int NATIVE = 900;
    public static final int MODULE = 800;
    public static final int CUSTOM = 700;

    public static <K,V> Map<K,V> getPriorityMap(Multimap<K,V> items, Map<V,Integer> priorities) {
        Map<K,V> priorityMap = new HashMap<>();
        for (Map.Entry<K,Collection<V>> writerEntry : items.asMap().entrySet()) {
            writerEntry.getValue().stream()
                    .min(Comparator.comparingInt(priorities::get))
                    .ifPresent((c) -> priorityMap.put(writerEntry.getKey(), c));
        }
        return priorityMap;
    }
}
