package dev.andstuff.kraken.example;

import static dev.andstuff.kraken.example.helper.CredentialsHelper.readFromFile;

import java.time.Instant;

import dev.andstuff.kraken.api.KrakenAPI;
import dev.andstuff.kraken.api.endpoint.account.params.ReportType;
import dev.andstuff.kraken.api.endpoint.account.params.RequestReportParams;
import dev.andstuff.kraken.api.rest.KrakenCredentials;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EoyBalanceExample {

    public static void main(String[] args) {

        KrakenCredentials credentials = readFromFile("/api-keys.properties");
        KrakenAPI api = new KrakenAPI(credentials);

        String reportId = api
                .requestReport(RequestReportParams.builder()
                        .type(ReportType.LEDGERS)
                        .description("Test " + Instant.now())
                        .fromDate(Instant.MIN)
                        .build())
                .reportId();
        log.info("Report ID: {}", reportId);
    }
}
