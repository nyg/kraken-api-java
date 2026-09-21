package dev.andstuff.kraken.api.endpoint.funding.response;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

final class DepositLimitDeserializer extends StdDeserializer<DepositLimit> {

    DepositLimitDeserializer() {
        super(DepositLimit.class);
    }

    @Override
    public DepositLimit deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.hasToken(JsonToken.VALUE_FALSE)) {
            return new DepositLimit(null, true);
        }
        if (parser.currentToken().isNumeric()) {
            return new DepositLimit(parser.getDecimalValue(), false);
        }
        if (parser.hasToken(JsonToken.VALUE_STRING)) {
            try {
                return new DepositLimit(new BigDecimal(parser.getText()), false);
            }
            catch (NumberFormatException _) {
                return context.reportInputMismatch(DepositLimit.class, "Expected a decimal deposit limit or false");
            }
        }
        return (DepositLimit) context.handleUnexpectedToken(DepositLimit.class, parser);
    }
}
