package com.hnv99.forum.core.util;

import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class for working with maps.
 */
public class MapUtils {

    /**
     * Creates a map with the given key-value pairs.
     *
     * @param k    The first key.
     * @param v    The first value.
     * @param kvs  Additional key-value pairs (key1, value1, key2, value2, ...).
     * @param <K>  Type of keys.
     * @param <V>  Type of values.
     * @return     A map containing the specified key-value pairs.
     */
    public static <K, V> Map<K, V> create(K k, V v, Object... kvs) {
        Map<K, V> map = Maps.newHashMapWithExpectedSize(kvs.length + 1);
        map.put(k, v);
        for (int i = 0; i < kvs.length; i += 2) {
            map.put((K) kvs[i], (V) kvs[i + 1]);
        }
        return map;
    }

    /**
     * Converts a collection to a map using the specified key and value mappers.
     *
     * @param list  The collection to be converted.
     * @param key   The function to extract keys from elements of the collection.
     * @param val   The function to extract values from elements of the collection.
     * @param <T>   Type of elements in the collection.
     * @param <K>   Type of keys in the resulting map.
     * @param <V>   Type of values in the resulting map.
     * @return      A map containing elements of the collection, where keys and values are obtained by applying the given functions.
     */
    public static <T, K, V> Map<K, V> toMap(Collection<T> list, Function<T, K> key, Function<T, V> val) {
        if (CollectionUtils.isEmpty(list)) {
            return Maps.newHashMapWithExpectedSize(0);
        }
        return list.stream().collect(Collectors.toMap(key, val));
    }
}
