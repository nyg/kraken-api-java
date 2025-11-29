package dev.andstuff.kraken.api.endpoint.account.response;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.andstuff.kraken.api.endpoint.account.params.ReportFormat;

public record Report(String id,
                     @JsonProperty("descr") String description,
                     ReportFormat format,
                     String subType,
                     Status status,
                     String fields,
                     @JsonProperty("createdtm") Instant requestDate,
                     @JsonProperty("starttm") Instant creationDate,
                     @JsonProperty("completedtm") Instant completionDate,
                     @JsonProperty("datastarttm") Instant reportFromDate,
                     @JsonProperty("dataendtm") Instant reportToDate,
                     String asset) {

    enum Status {
        QUEUED,
        PROCESSING,
        PROCESSED,
        @JsonEnumDefaultValue
        UNKNOWN
    }

    public boolean isProcessed() {
        return status == Status.PROCESSED;
    }
}
