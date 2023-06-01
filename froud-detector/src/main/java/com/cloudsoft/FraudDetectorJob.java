package com.cloudsoft;

import com.amazonaws.services.kinesisanalytics.runtime.KinesisAnalyticsRuntime;
import com.cloudsoft.config.JobProperties;
import com.cloudsoft.model.Alert;
import com.cloudsoft.model.LockedCard;
import com.cloudsoft.model.Transaction;
import com.cloudsoft.tasks.ExcessiveTransactionsFilter;
import com.cloudsoft.tasks.LockedCardsProcessFunction;
import com.cloudsoft.tasks.ScamDetector;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import static com.cloudsoft.StreamSinkFactory.createAlertSink;
import static com.cloudsoft.StreamSinkFactory.createLockedCardsSource;
import static com.cloudsoft.StreamSinkFactory.createTransactionsSource;
import static com.cloudsoft.tasks.LockedCardsProcessFunction.lockedCardMapStateDescriptor;

public class FraudDetectorJob {
    public JobExecutionResult execute() throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        JobProperties jobProperties = new JobProperties(
                KinesisAnalyticsRuntime.getApplicationProperties().get("FraudDetectorConfiguration"));

        WatermarkStrategy<Transaction> watermarkStrategy = WatermarkStrategy.<Transaction>forMonotonousTimestamps()
                .withTimestampAssigner((e, t) -> e.getTransactionTs());

        DataStream<LockedCard> lockedCardsStream = createLockedCardsSource(env, jobProperties);

        DataStream<Transaction> transactionsStream = createTransactionsSource(env, jobProperties)
                .assignTimestampsAndWatermarks(watermarkStrategy).name("transactions-with-watermark");

        BroadcastStream<LockedCard> lockedCardBroadcastStream = lockedCardsStream.broadcast(lockedCardMapStateDescriptor);

        DataStream<Alert> lockedCardsAlert = transactionsStream.keyBy(Transaction::getCustomerId)
                .connect(lockedCardBroadcastStream)
                .process(new LockedCardsProcessFunction())
                .name("locked-cards-process-function");

        DataStream<Alert> excessiveTransactions = transactionsStream
                .map(new MapFunction<Transaction, Tuple2<Transaction, Integer>>() {
                    @Override
                    public Tuple2<Transaction, Integer> map(Transaction value) {
                        return new Tuple2<>(value, 1);
                    }
                })
                .name("transaction-tuple-mapper")
                .keyBy(t -> t.f0.getCustomerId())
                .window(TumblingEventTimeWindows.of(Time.seconds(jobProperties.getExcessiveTransactionWindow())))
                .sum(1)
                .name("transaction-counter")
                .flatMap(new ExcessiveTransactionsFilter(jobProperties.getExcessiveTransactionCount()))
                .name("excessive-transactions-filter");

        DataStream<Alert> scammedTransactions = transactionsStream
                .keyBy(Transaction::getCustomerId)
                .process(new ScamDetector(jobProperties.getScamDetectorSmallAmount(),
                        jobProperties.getScamDetectorLargeAmount(), jobProperties.getScamDetectorTime()))
                .name("scam-detector");

        lockedCardsAlert.union(excessiveTransactions, scammedTransactions).sinkTo(createAlertSink(jobProperties));

        return env.execute("Fraud detector");
    }
}