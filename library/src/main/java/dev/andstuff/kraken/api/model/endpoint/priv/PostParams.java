package dev.andstuff.kraken.api.model.endpoint.priv;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;

public abstract class PostParams {

    protected abstract Map<String, String> params();

    private String nonce;

    public String initNonce() {
        nonce = Long.toString(System.currentTimeMillis());
        return nonce;
    }

    // TODO handle nested props
    public String encoded() {

        Map<String, String> params = params();
        params.put("nonce", nonce);

        return params.keySet().stream()
                .reduce(
                        new StringBuilder(),
                        (postData, key) -> postData.append(key)
                                .append("=")
                                .append(URLEncoder.encode(params.get(key), StandardCharsets.UTF_8))
                                .append("&"),
                        StringBuilder::append)
                .toString()
                .replaceFirst("&$", "");
    }

    protected static <T> void putIfNonNull(Map<String, String> map, String key, T value) {
        putIfNonNull(map, key, value, Object::toString);
    }

    protected static <T> void putIfNonNull(Map<String, String> map, String key, T value, Function<T, String> apply) {
        if (value != null) {
            map.put(key, apply.apply(value));
        }
    }
}
