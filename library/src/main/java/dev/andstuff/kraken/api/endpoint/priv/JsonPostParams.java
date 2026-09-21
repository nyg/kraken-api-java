package dev.andstuff.kraken.api.endpoint.priv;

import java.math.BigInteger;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

/**
 * The body parameters of a {@link PrivateEndpoint} sent as a JSON object, for endpoints whose parameters contain arrays or nested objects. The nonce is written as a JSON number, so the {@link dev.andstuff.kraken.api.rest.KrakenNonceGenerator KrakenNonceGenerator} must return an unsigned 64-bit integer in canonical decimal form.
 */
public abstract class JsonPostParams extends PostParams {

    private static final JsonMapper OBJECT_MAPPER = JsonMapper.builder().build();

    /**
     * Encodes the parameters and the nonce as a JSON object.
     *
     * @param params the parameters, nonce included
     * @return the JSON request body
     * @throws IllegalStateException if the nonce is not an unsigned 64-bit integer in canonical decimal form, or if a parameter cannot be serialized
     */
    @Override
    protected String encode(Map<String, Object> params) {
        String nonce = (String) params.get("nonce");
        BigInteger numericNonce = nonce != null && nonce.matches("0|[1-9]\\d{0,19}") ? new BigInteger(nonce) : null;
        if (numericNonce == null || numericNonce.bitLength() > 64) {
            throw new IllegalStateException("%s requires KrakenNonceGenerator to return an unsigned 64-bit integer in canonical decimal form".formatted(getClass().getSimpleName()));
        }
        params.put("nonce", numericNonce);
        try {
            return OBJECT_MAPPER.writeValueAsString(params);
        }
        catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot encode %s".formatted(getClass().getSimpleName()), e);
        }
    }
}
