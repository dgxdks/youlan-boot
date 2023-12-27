package com.youlan.common.core.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;

import java.io.IOException;

public class LongSerializer extends NumberSerializer {

    /**
     * 来源于 Number.MAX_SAFE_INTEGER
     */
    private static final long JS_MAX_SAFE_INTEGER = 9007199254740991L;

    /**
     * 来源于 Number.MIN_SAFE_INTEGER
     */
    private static final long JS_MIN_SAFE_INTEGER = -9007199254740991L;

    public LongSerializer(Class<? extends Number> rawType) {
        super(rawType);
    }

    @Override
    public void serialize(Number number, JsonGenerator generator, SerializerProvider provider) throws IOException {
        if (number.longValue() > JS_MIN_SAFE_INTEGER && number.longValue() < JS_MAX_SAFE_INTEGER) {
            super.serialize(number, generator, provider);
        } else {
            generator.writeString(number.toString());
        }
    }
}
