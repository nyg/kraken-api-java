package dev.andstuff.kraken.api.endpoint.subaccount.response;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The response of the {@code AccountTransfer} endpoint.
 *
 * @param transferId the identifier of the transfer
 * @param status the status of the transfer
 */
public record AccountTransfer(@JsonProperty("transfer_id") String transferId,
                              Status status) {

    /**
     * The status of a transfer between accounts.
     */
    public enum Status {
        PENDING,
        COMPLETE,

        @JsonEnumDefaultValue
        UNKNOWN
    }
}
