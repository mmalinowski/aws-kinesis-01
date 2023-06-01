package com.cloudsoft.tasks;

import com.cloudsoft.model.Alert;
import com.cloudsoft.model.Transaction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class ExcessiveTransactionsFilter implements FlatMapFunction<Tuple2<Transaction, Integer>, Alert> {
    private final int threshold;

    public ExcessiveTransactionsFilter(final int threshold) {
        this.threshold = threshold;
    }

    public void flatMap(Tuple2<Transaction, Integer> value, Collector<Alert> out) {
        if (value.f1 >= threshold) {
            out.collect(new Alert("EXCESSIVE_TRANSACTIONS", "Excessive transactions for customer=" + value.f0.getCustomerId()));
        }
    }
}
