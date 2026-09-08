package dev.andstuff.kraken.api.endpoint.account.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The wallet accounts returned by {@code ListWalletAccounts}.
 *
 * @param accounts the accounts
 * @param cursor the cursor
 */
public record WalletAccounts(List<Account> accounts,
        Cursor cursor) {

    /**
     * The flags returned by {@code ListWalletAccounts}.
     *
     * @param userDefined the user defined
     * @param active the active
     */
    public record Flags(@JsonProperty("user_defined") Boolean userDefined,
            Boolean active) {}

    /**
     * The status values used by the {@code ListWalletAccounts} endpoint.
     */
    public enum Status {
        @JsonProperty("active") ACTIVE,
        @JsonProperty("disabled") DISABLED,
        @JsonProperty("closed") CLOSED,
        @JsonEnumDefaultValue UNKNOWN
    }

    /**
     * The type values used by the {@code ListWalletAccounts} endpoint.
     */
    public enum Type {
        @JsonProperty("main") MAIN,
        @JsonProperty("spot") SPOT,
        @JsonProperty("pay") PAY,
        @JsonProperty("prop_paper") PROP_PAPER,
        @JsonProperty("prop_real") PROP_REAL,
        @JsonEnumDefaultValue UNKNOWN
    }

    /**
     * The account returned by {@code ListWalletAccounts}.
     *
     * @param accountId the account id
     * @param flags the flags
     * @param status the status
     * @param type the type
     * @param name the name
     */
    public record Account(@JsonProperty("account_id") String accountId,
            Flags flags,
            Status status,
            Type type,
            String name) {}

    /**
     * The cursor returned by {@code ListWalletAccounts}.
     *
     * @param next the next
     */
    public record Cursor(String next) {}
}
