package com.cloudsoft.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

public class LockedCardDeserializer implements DeserializationSchema<LockedCard> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public LockedCard deserialize(byte[] message) throws IOException {
        return mapper.readValue(message, LockedCard.class);
    }

    @Override
    public boolean isEndOfStream(LockedCard nextElement) {
        return false;
    }

    @Override
    public TypeInformation<LockedCard> getProducedType() {
        return TypeInformation.of(LockedCard.class);
    }
}
