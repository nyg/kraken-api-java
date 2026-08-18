package dev.andstuff.kraken.api.endpoint.account.params;

/**
 * The kind of removal asked for by the {@code RemoveExport} endpoint: canceling a report being generated, or deleting a processed one.
 */
public enum RemovalType {
    CANCEL, DELETE
}
