package dev.andstuff.kraken.api.model.endpoint.priv;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class GenericPostParams implements PostParams {

    private final Map<String, String> params;

    public GenericPostParams(Map<String, String> params) {
        this.params = new HashMap<>(params);
    }

    @Override
    public String initNonce() {
        String nonce = Long.toString(System.currentTimeMillis());
        params.put("nonce", nonce);
        return nonce;
    }

    @Override
    public String encoded() {
        // TODO handle nested props
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
}
