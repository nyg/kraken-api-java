package dev.andstuff.kraken.api.endpoint.account;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import dev.andstuff.kraken.api.endpoint.account.csv.RecordMappingStrategy;
import dev.andstuff.kraken.api.endpoint.account.params.ReportDataParams;
import dev.andstuff.kraken.api.endpoint.account.response.LedgerEntry;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;
import lombok.extern.slf4j.Slf4j;

/**
 * The private {@code RetrieveExport} endpoint, downloading a processed report. The response is a ZIP archive containing a single CSV file, which is parsed into ledger entries.
 */
@Slf4j
public class ReportDataEndpoint extends PrivateEndpoint<List<LedgerEntry>> {

    /**
     * Creates the endpoint.
     *
     * @param params the identifier of the report to download
     */
    public ReportDataEndpoint(ReportDataParams params) {
        super("RetrieveExport", params, new TypeReference<>() {});
    }

    /**
     * Parses the CSV file contained in the archive returned by Kraken.
     *
     * @param zipStream the response body
     * @return the ledger entries of the report, empty if the archive contains no file
     * @throws IOException if the archive cannot be read
     */
    @Override
    public List<LedgerEntry> processZipResponse(ZipInputStream zipStream) throws IOException {
        ZipEntry zipEntry = zipStream.getNextEntry();
        if (zipEntry == null) {
            log.error("No entries found in zip stream, report will be empty");
            return List.of();
        }

        log.info("Processing zip entry: {}", zipEntry.getName());
        InputStreamReader streamReader = new InputStreamReader(zipStream);

        CsvToBean<LedgerEntry> csvToBean = new CsvToBeanBuilder<LedgerEntry>(streamReader)
                .withMappingStrategy(new RecordMappingStrategy<>(LedgerEntry.class))
                .withIgnoreLeadingWhiteSpace(true)
                .withIgnoreEmptyLine(true)
                .build();

        return csvToBean.parse();
    }
}
