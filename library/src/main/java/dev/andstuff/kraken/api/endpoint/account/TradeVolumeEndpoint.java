package dev.andstuff.kraken.api.endpoint.account;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;

import dev.andstuff.kraken.api.endpoint.account.params.TradeVolumeParams;
import dev.andstuff.kraken.api.endpoint.account.response.TradeVolume;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code TradeVolume} endpoint for trade volume.
 */
public class TradeVolumeEndpoint extends PrivateEndpoint<TradeVolume> {

    private static final JsonMapper OBJECT_MAPPER = JsonMapper.builder().build();

    /**
     * Creates the {@code TradeVolume} endpoint with default options.
     */
    public TradeVolumeEndpoint() {
        this(TradeVolumeParams.builder().build());
    }

    /**
     * Creates the {@code TradeVolume} endpoint.
     *
     * @param params the request parameters
     */
    public TradeVolumeEndpoint(TradeVolumeParams params) {
        super("TradeVolume", params, new TypeReference<>() {});
    }

    /**
     * Encodes {@code TradeVolume} as JSON so class-qualified pair arrays retain their structure.
     *
     * @param nonce the request nonce
     * @return the JSON request body that must be signed and sent unchanged
     * @throws IllegalArgumentException if both pair formats are supplied
     */
    @Override
    public String encodedParamsWith(String nonce) {
        TradeVolumeParams params = (TradeVolumeParams) getPostParams();
        if (params.getPairs() != null && params.getPairsWithClass() != null) {
            throw new IllegalArgumentException("Specify pairs or pairsWithClass, not both");
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("nonce", new BigInteger(nonce));
        if (params.getPairs() != null) {
            body.put("pair", String.join(",", params.getPairs()));
        }
        if (params.getPairsWithClass() != null) {
            body.put("pair", params.getPairsWithClass());
        }
        if (params.getFeeInfo() != null) {
            body.put("fee-info", params.getFeeInfo());
        }
        if (params.getFeeSchedule() != null) {
            body.put("fee_schedule", params.getFeeSchedule());
        }
        if (params.getRebaseMultiplier() != null) {
            body.put("rebase_multiplier", params.getRebaseMultiplier().getValue());
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(body);
        }
        catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot encode TradeVolume parameters", e);
        }
    }

    /**
     * Returns the media type used by the {@code TradeVolume} request body.
     *
     * @return {@code application/json}
     */
    @Override
    public String getContentType() {
        return "application/json";
    }
}
