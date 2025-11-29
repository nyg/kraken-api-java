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

@Slf4j
public class ReportDataEndpoint extends PrivateEndpoint<List<LedgerEntry>> {

    public ReportDataEndpoint(ReportDataParams params) {
        super("RetrieveExport", params, new TypeReference<>() {});
    }

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
