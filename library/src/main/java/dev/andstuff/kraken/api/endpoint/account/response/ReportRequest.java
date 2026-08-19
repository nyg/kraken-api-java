package dev.andstuff.kraken.api.endpoint.account.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The response of the {@code AddExport} endpoint.
 *
 * @param reportId the identifier of the requested report, needed to follow its generation and download it
 */
public record ReportRequest(@JsonProperty("id") String reportId) {
}
