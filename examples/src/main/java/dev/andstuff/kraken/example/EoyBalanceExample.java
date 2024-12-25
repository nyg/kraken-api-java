package dev.andstuff.kraken.example;

import static dev.andstuff.kraken.example.helper.CredentialsHelper.readFromFile;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

import dev.andstuff.kraken.api.KrakenAPI;
import dev.andstuff.kraken.api.endpoint.account.params.ReportType;
import dev.andstuff.kraken.api.endpoint.account.params.RequestReportParams;
import dev.andstuff.kraken.api.rest.KrakenCredentials;
import dev.andstuff.kraken.example.eoy.EoyBalance;
import dev.andstuff.kraken.example.eoy.EoyBalanceSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class EoyBalanceExample {

    private final KrakenAPI api;

    public static void main(String[] args) {
        KrakenCredentials credentials = readFromFile("/api-keys.properties");
        new EoyBalanceExample(new KrakenAPI(credentials))
                .generate(Instant.parse("2024-01-01T00:00:00Z"), "eoy-balance.csv");
    }

    public void generate(Instant dateTo, String reportFileName) {
        String reportId = requestReport(dateTo);
        waitUntilReportIsProcessed(reportId);

        EoyBalance eoyBalance = new EoyBalance(api.reportData(reportId));
        new EoyBalanceSummary(eoyBalance).writeToFile(reportFileName);

        log.info("Report was removed: {}", api.deleteReport(reportId));
    }

    private String requestReport(Instant dateTo) {
        String reportId = api
                .requestReport(RequestReportParams.builder()
                        .type(ReportType.LEDGERS)
                        .description("kraken-api-examples-" + Instant.now())
                        .fromDate(Instant.EPOCH)
                        .toDate(dateTo)
                        .build())
                .reportId();
        log.info("Report requested with id: {}", reportId);
        return reportId;
    }

    private void waitUntilReportIsProcessed(String reportId) {
        AtomicBoolean processed = new AtomicBoolean(false);
        while (!processed.get()) {
            try {
                Thread.sleep(5_000);
            }
            catch (InterruptedException e) {
                log.warn("Thread interrupted while waiting for the report to be processed", e);
                Thread.currentThread().interrupt();
                // continue fetching the report status
            }

            api.reportsStatuses(ReportType.LEDGERS).stream()
                    .filter(report -> report.id().equals(reportId))
                    .findAny()
                    .ifPresentOrElse(
                            report -> {
                                log.info("Report status is: {}", report.status());
                                processed.set(report.isProcessed());
                            },
                            () -> log.info("Report not (yet) processed"));
        }
    }
}
