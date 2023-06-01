package com.cloudsoft;

import com.cloudsoft.config.JobProperties;
import com.cloudsoft.model.Alert;
import com.cloudsoft.model.AlertSerializer;
import com.cloudsoft.model.LockedCard;
import com.cloudsoft.model.LockedCardDeserializer;
import com.cloudsoft.model.Transaction;
import com.cloudsoft.model.TransactionDeserializer;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.connector.kinesis.sink.KinesisStreamsSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer;

import java.util.Properties;

import static org.apache.flink.kinesis.shaded.org.apache.flink.connector.aws.config.AWSConfigConstants.AWS_REGION;

public final class StreamSinkFactory {

    private StreamSinkFactory() {
    }
    public static DataStream<Transaction> createTransactionsSource(
            final StreamExecutionEnvironment env,
            final JobProperties jobProperties) {
        return createSource(env, jobProperties.getTransactionsStream(), "transactions", jobProperties.getRegion(),
                new TransactionDeserializer());
    }

    public static DataStream<LockedCard> createLockedCardsSource(
            final StreamExecutionEnvironment env,
            final JobProperties jobProperties) {
        return createSource(env, jobProperties.getLockedCardsStream(), "locked-cards", jobProperties.getRegion(),
                new LockedCardDeserializer());
    }

    private static <T> DataStream<T> createSource(
            final StreamExecutionEnvironment env,
            final String streamName,
            final String name,
            final String region,
            final DeserializationSchema<T> deserializationSchema) {
        Properties inputProperties = new Properties();
        inputProperties.setProperty(AWS_REGION, region);
        return env.addSource(new FlinkKinesisConsumer<>(streamName, deserializationSchema, inputProperties)).name(name);
    }

    public static KinesisStreamsSink<Alert> createAlertSink(final JobProperties jobProperties) {
        Properties outputProperties = new Properties();
        outputProperties.setProperty(AWS_REGION, jobProperties.getRegion());

        return KinesisStreamsSink.<Alert>builder()
                .setKinesisClientProperties(outputProperties)
                .setSerializationSchema(new AlertSerializer())
                .setStreamName(jobProperties.getAlertsStream())
                .setPartitionKeyGenerator(element -> String.valueOf(element.hashCode()))
                .build();
    }
}
