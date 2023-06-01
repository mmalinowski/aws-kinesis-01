package com.cloudsoft.tasks;

import com.cloudsoft.model.Alert;
import com.cloudsoft.model.Transaction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.io.IOException;

public class ScamDetector extends KeyedProcessFunction<String, Transaction, Alert> {
    private transient ValueState<Boolean> smallTransactionFlagState;
    private transient ValueState<Long> timerState;

    private final int smallAmount;
    private final int largeAmount;
    private final long time;

    public ScamDetector(int smallAmount, int largeAmount, long time) {
        this.smallAmount = smallAmount;
        this.largeAmount = largeAmount;
        this.time = time;
    }

    /**
     *  Inits the state - it's called before actual processing. The state will be store in execution context
     */
    @Override
    public void open(Configuration parameters) {
        ValueStateDescriptor<Boolean> flagDescriptor = new ValueStateDescriptor<>(
                "small_transaction", Types.BOOLEAN);
        smallTransactionFlagState = getRuntimeContext().getState(flagDescriptor);

        ValueStateDescriptor<Long> timerDescriptor = new ValueStateDescriptor<>(
                "timer-state",
                Types.LONG);
        timerState = getRuntimeContext().getState(timerDescriptor);
    }

    @Override
    public void processElement(Transaction transaction, KeyedProcessFunction<String, Transaction, Alert>.Context context, Collector<Alert> collector) throws Exception {
        Boolean previousTransactionWasSmall = smallTransactionFlagState.value();

        // Check if the flag is set
        if (previousTransactionWasSmall != null) {
            if (transaction.getAmount() > largeAmount) {
                collector.collect(new Alert("SCAM", "Suspicious operations for customer " + transaction.getCustomerId()));
            }
            cleanUp(context);
        }

        if (transaction.getAmount() <= smallAmount) {
            // Set the flag to true
            smallTransactionFlagState.update(true);

            // set the timer and timer state
            long timer = context.timerService().currentProcessingTime() + (time * 1000);
            context.timerService().registerProcessingTimeTimer(timer);
            timerState.update(timer);
        }
    }

    @Override
    public void onTimer(long timestamp, OnTimerContext ctx, Collector<Alert> out) {
        timerState.clear();
        smallTransactionFlagState.clear();
    }

    private void cleanUp(Context context) throws IOException {
        Long timer = timerState.value();
        context.timerService().deleteProcessingTimeTimer(timer);

        timerState.clear();
        smallTransactionFlagState.clear();
    }
}
