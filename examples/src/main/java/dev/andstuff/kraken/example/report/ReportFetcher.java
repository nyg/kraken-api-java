package dev.andstuff.kraken.example.report;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import dev.andstuff.kraken.api.KrakenAPI;
import dev.andstuff.kraken.api.endpoint.account.params.ReportType;
import dev.andstuff.kraken.api.endpoint.account.params.RequestReportParams;
import dev.andstuff.kraken.api.endpoint.account.response.LedgerEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ReportFetcher {

    private final KrakenAPI api;

    public String requestReport(Instant dateTo) {
        return api.requestReport(RequestReportParams.builder()
                        .type(ReportType.LEDGERS)
                        .description("kraken-api-examples-" + Instant.now())
                        .fromDate(Instant.EPOCH)
                        .toDate(dateTo)
                        .build())
                .reportId();
    }

    public List<LedgerEntry> fetchReportData(String reportId) {
        AtomicBoolean processed = new AtomicBoolean(false);
        while (!processed.get()) {
            try {
                Thread.sleep(1_000);
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

        return api.reportData(reportId);
    }

    public void deleteReport(String reportId) {
        boolean result = api.deleteReport(reportId);
        log.info("Removed report: {}", result);
    }
}
