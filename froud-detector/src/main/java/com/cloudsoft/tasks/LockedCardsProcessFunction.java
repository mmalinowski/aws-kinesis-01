package com.cloudsoft.tasks;

import com.cloudsoft.model.Alert;
import com.cloudsoft.model.LockedCard;
import com.cloudsoft.model.Transaction;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;

public class LockedCardsProcessFunction extends KeyedBroadcastProcessFunction<String, Transaction, LockedCard, Alert> {
    public static final MapStateDescriptor<String, LockedCard> lockedCardMapStateDescriptor =
            new MapStateDescriptor<>("locked_cards", BasicTypeInfo.STRING_TYPE_INFO, Types.POJO(LockedCard.class));

    @Override
    public void processElement(Transaction transaction, KeyedBroadcastProcessFunction<String, Transaction, LockedCard, Alert>.ReadOnlyContext ctx, Collector<Alert> out) throws Exception {
        if (ctx.getBroadcastState(lockedCardMapStateDescriptor).contains(transaction.getCardNumber())) {
            out.collect(new Alert("LOCKED_CARD", "Suspicious transaction " + transaction));
        }
    }

    @Override
    public void processBroadcastElement(LockedCard card, KeyedBroadcastProcessFunction<String, Transaction, LockedCard, Alert>.Context ctx, Collector<Alert> out) throws Exception {
        ctx.getBroadcastState(lockedCardMapStateDescriptor).put(card.getCardNumber(), card);
    }
}
