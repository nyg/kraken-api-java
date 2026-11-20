package dev.andstuff.kraken.api.endpoint.account.params;

final class CursorPagination {

    private CursorPagination() {}

    static void validate(Boolean withCursor, String cursor, Integer offset) {
        boolean cursorPaging = Boolean.TRUE.equals(withCursor);
        if (cursor != null && !cursorPaging) {
            throw new IllegalArgumentException("cursor requires withCursor to be true");
        }
        if (cursorPaging && offset != null && offset != 0) {
            throw new IllegalArgumentException("offset must be absent or 0 when withCursor is true");
        }
    }
}
