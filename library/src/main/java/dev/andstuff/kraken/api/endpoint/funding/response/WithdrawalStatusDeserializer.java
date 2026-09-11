package dev.andstuff.kraken.api.endpoint.funding.response;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

final class WithdrawalStatusDeserializer extends StdDeserializer<WithdrawalStatus> {

    WithdrawalStatusDeserializer() {
        super(WithdrawalStatus.class);
    }

    @Override
    public WithdrawalStatus deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JavaType listType = context.getTypeFactory().constructCollectionType(List.class, Withdrawal.class);
        if (parser.isExpectedStartArrayToken()) {
            return new WithdrawalStatus(context.readValue(parser, listType), null);
        }
        if (!parser.isExpectedStartObjectToken()) {
            return (WithdrawalStatus) context.handleUnexpectedToken(WithdrawalStatus.class, parser);
        }
        List<Withdrawal> entries = null;
        String nextCursor = null;
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String field = parser.currentName();
            parser.nextToken();
            switch (field) {
                case "withdrawals", "withdrawal" -> {
                    if (parser.isExpectedStartObjectToken()) {
                        entries = List.of(context.readValue(parser, Withdrawal.class));
                    }
                    else {
                        entries = context.readValue(parser, listType);
                    }
                }
                case "next_cursor" -> nextCursor = context.readValue(parser, String.class);
                default -> parser.skipChildren();
            }
        }
        if (entries == null) {
            return context.reportInputMismatch(WithdrawalStatus.class, "Expected a withdrawals list in the paginated response");
        }
        return new WithdrawalStatus(entries, nextCursor);
    }
}
