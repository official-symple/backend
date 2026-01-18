package com.DreamOfDuck.subscription.service.verify.dto.appstore;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Accepts boolean or string ("true"/"false"/"1"/"0") and converts to Boolean.
 */
public class FlexibleBooleanDeserializer extends JsonDeserializer<Boolean> {
    @Override
    public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.currentToken();
        if (t == JsonToken.VALUE_TRUE) {
            return true;
        }
        if (t == JsonToken.VALUE_FALSE) {
            return false;
        }
        if (t == JsonToken.VALUE_STRING) {
            String s = p.getText();
            if (s == null) {
                return null;
            }
            s = s.trim().toLowerCase();
            if (s.isEmpty()) {
                return null;
            }
            if ("true".equals(s) || "1".equals(s)) {
                return true;
            }
            if ("false".equals(s) || "0".equals(s)) {
                return false;
            }
        }
        return null;
    }
}


