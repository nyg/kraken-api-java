package dev.andstuff.kraken.api.endpoint.market;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.market.response.MaintenanceSchedule;
import dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint;

public class MaintenanceScheduleEndpoint extends PublicEndpoint<MaintenanceSchedule> {

    public MaintenanceScheduleEndpoint() {
        super("MaintenanceSchedule", new TypeReference<>() {});
    }
}
