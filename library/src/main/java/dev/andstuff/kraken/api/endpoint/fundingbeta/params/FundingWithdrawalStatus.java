package dev.andstuff.kraken.api.endpoint.fundingbeta.params;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The status of a withdrawal of the Funding (Beta) API.
 */
@Getter
@RequiredArgsConstructor
public enum FundingWithdrawalStatus {
    PENDING("pending"),
    SUCCESS("success"),
    FAILED("failed"),
    @JsonEnumDefaultValue UNKNOWN("unknown");

    @JsonValue
    private final String value;
}
