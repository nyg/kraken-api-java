package dev.andstuff.kraken.api.endpoint.priv;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import lombok.AccessLevel;
import lombok.Setter;

/**
 * The body parameters of a {@link PrivateEndpoint}, form-encoded by default. The nonce is set by the library just before the request is signed.
 */
@Setter(AccessLevel.PACKAGE)
public abstract class PostParams {

    /**
     * Returns the parameters as name/value pairs, using the names expected by Kraken, without the nonce.
     *
     * @return the POST parameters
     */
    protected abstract Map<String, ?> params();

    private String nonce;

    /**
     * Returns the parameters and the nonce as a request body using the parameter type's encoding.
     *
     * @return the encoded request body
     */
    public String encoded() {
        Map<String, Object> params = new LinkedHashMap<>(params());
        params.put("nonce", nonce);
        return encode(params);
    }

    protected String encode(Map<String, Object> params) {
        return params.keySet().stream()
                .reduce(
                        new StringBuilder(),
                        (postData, key) -> postData.append(key)
                                .append("=")
                                .append(URLEncoder.encode(params.get(key).toString(), StandardCharsets.UTF_8))
                                .append("&"),
                        StringBuilder::append)
                .toString()
                .replaceFirst("&$", "");
    }

    /**
     * Adds a parameter to the given map, unless its value is {@code null}, using {@link Object#toString()} for its string representation.
     *
     * @param <T> the type of the parameter value
     * @param map the map to add the parameter to
     * @param key the parameter name, as expected by Kraken
     * @param value the parameter value, possibly {@code null}
     */
    protected static <T> void putIfNonNull(Map<String, String> map, String key, T value) {
        putIfNonNull(map, key, value, Object::toString);
    }

    /**
     * Adds a parameter to the given map, unless its value is {@code null}.
     *
     * @param <T> the type of the parameter value
     * @param map the map to add the parameter to
     * @param key the parameter name, as expected by Kraken
     * @param value the parameter value, possibly {@code null}
     * @param apply the function converting the value to its string representation
     */
    protected static <T> void putIfNonNull(Map<String, String> map, String key, T value, Function<T, String> apply) {
        if (value != null) {
            map.put(key, apply.apply(value));
        }
    }
}
