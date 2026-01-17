package com.DreamOfDuck.subscription.service.verify.dto.appstore;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Accepts epoch seconds or epoch millis as number or string and normalizes to epoch millis.
 */
public class FlexibleEpochMillisDeserializer extends JsonDeserializer<Long> {
    @Override
    public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.currentToken();
        if (t == JsonToken.VALUE_NUMBER_INT) {
            long v = p.getLongValue();
            return normalize(v);
        }
        if (t == JsonToken.VALUE_STRING) {
            String s = p.getText();
            if (s == null || s.isBlank()) {
                return null;
            }
            try {
                long v = Long.parseLong(s);
                return normalize(v);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private long normalize(long timestamp) {
        // Apple timestamps can be seconds or milliseconds.
        if (timestamp < 10_000_000_000L) {
            return timestamp * 1000;
        }
        return timestamp;
    }
}


