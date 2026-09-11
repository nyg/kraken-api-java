package dev.andstuff.kraken.api.endpoint.funding.response;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

final class DepositStatusDeserializer extends StdDeserializer<DepositStatus> {

    DepositStatusDeserializer() {
        super(DepositStatus.class);
    }

    @Override
    public DepositStatus deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JavaType listType = context.getTypeFactory().constructCollectionType(List.class, Deposit.class);
        if (parser.isExpectedStartArrayToken()) {
            return new DepositStatus(context.readValue(parser, listType), null);
        }
        if (!parser.isExpectedStartObjectToken()) {
            return (DepositStatus) context.handleUnexpectedToken(DepositStatus.class, parser);
        }
        List<Deposit> entries = null;
        String nextCursor = null;
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String field = parser.currentName();
            parser.nextToken();
            switch (field) {
                case "deposits", "deposit" -> {
                    if (parser.isExpectedStartObjectToken()) {
                        entries = List.of(context.readValue(parser, Deposit.class));
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
            return context.reportInputMismatch(DepositStatus.class, "Expected a deposits list in the paginated response");
        }
        return new DepositStatus(entries, nextCursor);
    }
}
