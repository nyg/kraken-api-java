package dev.andstuff.kraken.api.endpoint.account.response;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.andstuff.kraken.api.endpoint.account.params.ReportFormat;

/**
 * The status of a report, as returned by the {@code ExportStatus} endpoint.
 *
 * @param id the identifier of the report
 * @param description the description given when the report was requested
 * @param format the file format of the report
 * @param subType the sub type of the report
 * @param status the generation status of the report
 * @param fields the fields exported by the report
 * @param requestDate the time the report was requested
 * @param creationDate the time the generation of the report started
 * @param completionDate the time the generation of the report completed
 * @param reportFromDate the start of the period covered by the report
 * @param reportToDate the end of the period covered by the report
 * @param asset the asset the report is restricted to
 */
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

    /**
     * Returns whether the report has been generated and can be downloaded.
     *
     * @return whether the report is ready
     */
    public boolean isProcessed() {
        return status == Status.PROCESSED;
    }
}
