package dev.andstuff.kraken.api.endpoint.trading.response;

import java.time.Instant;

/**
 * The timer returned by {@code CancelAllOrdersAfter}, cancelling all orders of the account when it expires.
 *
 * @param currentTime the time Kraken received the request
 * @param triggerTime the time after which all orders are cancelled unless the timer is extended or disabled, null when the timer is disabled
 */
public record DeadMansSwitch(Instant currentTime,
                             Instant triggerTime) {

    /**
     * Creates the timer, mapping the {@code 0} trigger time Kraken returns for a disabled timer to null.
     *
     * @param currentTime the time Kraken received the request
     * @param triggerTime the trigger time, the epoch or null when the timer is disabled
     */
    public DeadMansSwitch {
        triggerTime = Instant.EPOCH.equals(triggerTime) ? null : triggerTime;
    }
}
