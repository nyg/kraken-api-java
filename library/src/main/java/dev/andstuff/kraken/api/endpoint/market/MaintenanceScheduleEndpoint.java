package dev.andstuff.kraken.api.endpoint.market;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.market.response.MaintenanceSchedule;
import dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint;

/**
 * The public {@code MaintenanceSchedule} endpoint, returning typed market data.
 */
public class MaintenanceScheduleEndpoint extends PublicEndpoint<MaintenanceSchedule> {

    /**
     * Creates the {@code MaintenanceSchedule} endpoint for scheduled events in the next seven days.
     */
    public MaintenanceScheduleEndpoint() {
        super("MaintenanceSchedule", new TypeReference<>() {});
    }
}
