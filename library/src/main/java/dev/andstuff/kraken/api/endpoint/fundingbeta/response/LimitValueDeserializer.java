package dev.andstuff.kraken.api.endpoint.fundingbeta.response;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

final class LimitValueDeserializer extends StdDeserializer<LimitValue> {

    LimitValueDeserializer() {
        super(LimitValue.class);
    }

    @Override
    public LimitValue deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.hasToken(JsonToken.START_OBJECT)) {
            return new LimitValue(null, context.readValue(parser, LimitValue.Amounts.class));
        }
        if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            return new LimitValue(parser.getLongValue(), null);
        }
        if (parser.hasToken(JsonToken.VALUE_STRING)) {
            try {
                return new LimitValue(Long.valueOf(parser.getText()), null);
            }
            catch (NumberFormatException e) {
                return context.reportInputMismatch(LimitValue.class, "Expected a count or limit amounts");
            }
        }
        return (LimitValue) context.handleUnexpectedToken(LimitValue.class, parser);
    }
}
