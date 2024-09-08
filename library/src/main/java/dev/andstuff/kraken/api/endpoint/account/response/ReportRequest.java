package dev.andstuff.kraken.api.endpoint.account.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReportRequest(@JsonProperty("id") String reportId) {
}
