package dev.andstuff.kraken.api.endpoint.pub;

import java.util.Map;
import java.util.function.Function;

public interface QueryParams {

    QueryParams EMPTY = Map::of;

    Map<String, String> toMap();

    static <T> void putIfNonNull(Map<String, String> map, String key, T value, Function<T, String> apply) {
        if (value != null) {
            map.put(key, apply.apply(value));
        }
    }
}
