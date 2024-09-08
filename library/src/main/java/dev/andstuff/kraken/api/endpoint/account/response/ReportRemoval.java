package dev.andstuff.kraken.api.endpoint.account.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReportRemoval(@JsonProperty("delete") boolean wasDeleted,
                            @JsonProperty("cancel") boolean wasCanceled) {
}
