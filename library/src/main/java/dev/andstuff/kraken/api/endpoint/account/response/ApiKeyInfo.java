package dev.andstuff.kraken.api.endpoint.account.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The api key info returned by {@code GetApiKeyInfo}.
 *
 * @param name the name
 * @param apiKey the api key
 * @param nonce the nonce
 * @param nonceWindow the nonce window
 * @param permissions the permissions
 * @param iban the iban
 * @param validUntil the valid until as Unix seconds, preserving Kraken's string representation
 * @param queryFrom the query from as Unix seconds, preserving Kraken's string representation
 * @param queryTo the query to as Unix seconds, preserving Kraken's string representation
 * @param createdTime the created time as Unix seconds, preserving Kraken's string representation
 * @param modifiedTime the modified time as Unix seconds, preserving Kraken's string representation
 * @param ipAllowlist the ip allowlist
 * @param lastUsed the last used as Unix seconds, preserving Kraken's string representation
 */
public record ApiKeyInfo(@JsonProperty("apiKeyName") String name,
        String apiKey,
        String nonce,
        Long nonceWindow,
        List<String> permissions,
        String iban,
        String validUntil,
        String queryFrom,
        String queryTo,
        String createdTime,
        String modifiedTime,
        List<String> ipAllowlist,
        String lastUsed) {}
