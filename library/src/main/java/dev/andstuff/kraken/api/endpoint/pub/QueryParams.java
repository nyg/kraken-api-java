package dev.andstuff.kraken.api.endpoint.pub;

import java.util.Map;
import java.util.function.Function;

/**
 * The URL query parameters of a {@link PublicEndpoint}.
 */
public interface QueryParams {

    /**
     * Parameters of an endpoint that takes none.
     */
    QueryParams EMPTY = Map::of;

    /**
     * Returns the parameters as name/value pairs, using the names expected by Kraken. Values are URL encoded by the endpoint.
     *
     * @return the query parameters
     */
    Map<String, String> toMap();

    /**
     * Adds a parameter to the given map, unless its value is {@code null}.
     *
     * @param <T> the type of the parameter value
     * @param map the map to add the parameter to
     * @param key the parameter name, as expected by Kraken
     * @param value the parameter value, possibly {@code null}
     * @param apply the function converting the value to its string representation
     */
    static <T> void putIfNonNull(Map<String, String> map, String key, T value, Function<T, String> apply) {
        if (value != null) {
            map.put(key, apply.apply(value));
        }
    }
}
