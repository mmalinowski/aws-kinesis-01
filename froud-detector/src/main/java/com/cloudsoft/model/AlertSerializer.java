package com.cloudsoft.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.serialization.SerializationSchema;

public class AlertSerializer implements SerializationSchema<Alert> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public byte[] serialize(Alert element) {
        try {
            return mapper.writeValueAsBytes(element);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to serialize object " + element.toString(), e);
        }
    }
}
