package dev.andstuff.kraken.api.endpoint.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.andstuff.kraken.api.endpoint.account.params.ReportDataParams;
import dev.andstuff.kraken.api.endpoint.account.response.LedgerEntry;

@ExtendWith(MockitoExtension.class)
class ReportDataEndpointTest {

    @Test
    void should_encode_report_identifier_when_downloading_report() {
        ReportDataEndpoint unit = new ReportDataEndpoint(ReportDataParams.of("TCJA"));

        String result = unit.encodedParamsWith("123");

        assertThat(result).isEqualTo("id=TCJA&nonce=123");
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/RetrieveExport");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
    }

    @Test
    void should_reject_missing_reportId_when_building_parameters() {
        assertThatThrownBy(() -> ReportDataParams.of(null)).isInstanceOf(NullPointerException.class).hasMessageContaining("reportId");
    }

    @Test
    void should_parse_every_ledger_column_when_reading_exported_archive() throws Exception {
        ReportDataEndpoint unit = new ReportDataEndpoint(ReportDataParams.of("TCJA"));
        ByteArrayOutputStream archive = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(archive)) {
            zip.putNextEntry(new ZipEntry("ledgers.csv"));
            zip.write("""
                    "txid","refid","time","type","subtype","aclass","subclass","asset","wallet","amount","fee","balance"
                    "L4UESK-KG3EQ-UFO4T5","TJKLXF-PGMUI-4NTLXU","2023-07-04 09:54:44","trade","","currency","fiat","ZGBP","spot / main",-24.5000,0.0490,459567.9171

                    "LREWARD-AAAAA-BBBBBB","RREWARD-CCCCC-DDDDDD","2024-01-01 00:00:00","earn","reward","currency","crypto","DOT28.S","earn / bonded",0.0000000000123456789,0,1.0000000000123456789
                    """.getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
        }

        List<LedgerEntry> result;
        try (ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(archive.toByteArray()))) {
            result = unit.processZipResponse(zipStream);
        }

        assertThat(result).hasSize(2);
        assertThat(result.getFirst()).isEqualTo(new LedgerEntry("L4UESK-KG3EQ-UFO4T5", "TJKLXF-PGMUI-4NTLXU", Instant.parse("2023-07-04T09:54:44Z"),
                LedgerEntry.Type.TRADE, "", "currency", "fiat", "ZGBP", "spot / main",
                new BigDecimal("-24.5000"), new BigDecimal("0.0490"), new BigDecimal("459567.9171")));
        assertThat(result.getLast()).satisfies(entry -> {
            assertThat(entry.isStakingReward()).isTrue();
            assertThat(entry.underlyingAsset()).isEqualTo("DOT");
            assertThat(entry.amount()).isEqualByComparingTo("0.0000000000123456789");
            assertThat(entry.year()).isEqualTo(2024);
        });
    }

    @Test
    void should_return_no_entries_when_archive_is_empty() throws Exception {
        ReportDataEndpoint unit = new ReportDataEndpoint(ReportDataParams.of("TCJA"));
        ByteArrayOutputStream archive = new ByteArrayOutputStream();
        new ZipOutputStream(archive).close();

        List<LedgerEntry> result;
        try (ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(archive.toByteArray()))) {
            result = unit.processZipResponse(zipStream);
        }

        assertThat(result).isEmpty();
    }
}
