package dev.andstuff.kraken.example;

import static dev.andstuff.kraken.example.helper.CredentialsHelper.readFromFile;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import dev.andstuff.kraken.api.KrakenAPI;
import dev.andstuff.kraken.api.endpoint.account.params.ReportType;
import dev.andstuff.kraken.api.endpoint.account.params.RequestReportParams;
import dev.andstuff.kraken.api.endpoint.account.response.Report;
import dev.andstuff.kraken.api.rest.KrakenCredentials;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EoyBalanceExample {

    public static void main(String[] args) throws InterruptedException {

        KrakenCredentials credentials = readFromFile("/api-keys.properties");
        KrakenAPI api = new KrakenAPI(credentials);

        // Request report
        String reportId = api
                .requestReport(RequestReportParams.builder()
                        .type(ReportType.LEDGERS)
                        .description("Test " + Instant.now())
                        .fromDate(Instant.MIN)
                        .build())
                .reportId();
        log.info("Report ID: {}", reportId);

        // Wait for report to be processed
        AtomicBoolean processed = new AtomicBoolean(false);
        while (!processed.get()) {
            Thread.sleep(5_000);

            Optional<Report> report = api.reportsStatuses(ReportType.LEDGERS).stream()
                    .filter(r -> r.id().equals(reportId))
                    .findAny();

            report.ifPresentOrElse(
                    r -> {
                        log.info("Report status is: {}", r.status());
                        processed.set(r.isProcessed());
                    },
                    () -> log.info("Report not (yet) created"));
        }

        // Download report
        api.reportData(reportId);

        // Delete report
        log.info("Report was removed: {}", api.deleteReport(reportId));
    }
}
