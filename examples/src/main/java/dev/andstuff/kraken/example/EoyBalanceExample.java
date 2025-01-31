package dev.andstuff.kraken.example;

import static dev.andstuff.kraken.example.helper.CredentialsHelper.readFromFile;

import java.time.Instant;
import java.util.List;

import dev.andstuff.kraken.api.KrakenAPI;
import dev.andstuff.kraken.api.endpoint.account.response.LedgerEntry;
import dev.andstuff.kraken.api.rest.KrakenCredentials;
import dev.andstuff.kraken.example.eoy.EoyBalanceSummary;
import dev.andstuff.kraken.example.eoy.EoyBalances;
import dev.andstuff.kraken.example.report.ReportFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Generates a CSV file containing your Kraken balances at any given date. EOY
 * means end-of-year, but the historical balances can be retrieved for any
 * date.
 * <p>
 * <i>Note:</i> does not include assets in the futures account.
 */
@Slf4j
@RequiredArgsConstructor
public class EoyBalanceExample {

    private final KrakenAPI api;

    public static void main(String[] args) {
        KrakenCredentials credentials = readFromFile("/api-keys.properties");
        Instant dateTo = Instant.parse(args.length > 1 ? args[1] : "2024-01-01T00:00:00Z");
        new EoyBalanceExample(new KrakenAPI(credentials))
                .generate(dateTo, "eoy-balance.csv", true, true, false);
    }

    /**
     * Generates the CSV report.
     *
     * @param dateTo            the date at which the balances should be extracted
     * @param reportFileName    the name of the CSV file
     * @param groupAssets       group by underlying asset, e.g. DOT28.S will be grouped with DOT
     * @param groupWallets      sum balances across all wallets, e.g. "main / spot", "earn / bonded", "earn / liquid"
     * @param thousandSeparator format balances with thousand separators
     */
    public void generate(Instant dateTo, String reportFileName, boolean groupAssets, boolean groupWallets, boolean thousandSeparator) {
        ReportFetcher reportFetcher = new ReportFetcher(api);
        String reportId = reportFetcher.requestReport(dateTo);
        List<LedgerEntry> ledgerEntries = reportFetcher.fetchReportData(reportId);
        reportFetcher.deleteReport(reportId);

        EoyBalances eoyBalance = new EoyBalances(ledgerEntries, groupAssets, groupWallets);
        new EoyBalanceSummary(eoyBalance).writeToFile(reportFileName, thousandSeparator);
    }
}
