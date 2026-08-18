package dev.andstuff.kraken.api.endpoint.account.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The response of the {@code RemoveExport} endpoint, only one of the two fields being returned by Kraken, depending on the kind of removal asked for.
 *
 * @param wasDeleted whether the report was deleted
 * @param wasCanceled whether the generation of the report was canceled
 */
public record ReportRemoval(@JsonProperty("delete") boolean wasDeleted,
                            @JsonProperty("cancel") boolean wasCanceled) {
}
