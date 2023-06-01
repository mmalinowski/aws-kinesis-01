package com.cloudsoft.config;

import java.util.Properties;

public class JobProperties {
    private final String transactionsStream;
    private final String lockedCardsStream;
    private final String alertsStream;
    private final String region;

    private final int excessiveTransactionWindow;
    private final int excessiveTransactionCount;

    private final int scamDetectorSmallAmount;
    private final int scamDetectorLargeAmount;
    private final int scamDetectorTime;


    public JobProperties(final Properties applicationProperties) {
        this.region = applicationProperties.getProperty("region", "eu-west-1");
        this.transactionsStream = applicationProperties.getProperty("transactionsStreamName", "transactions");
        this.lockedCardsStream = applicationProperties.getProperty("lockedCardsStreamName", "locked-cards");
        this.alertsStream = applicationProperties.getProperty("alertsStreamName", "transaction-alerts");
        this.excessiveTransactionWindow = getInt(applicationProperties,"excessiveTransactionWindow", "15");
        this.excessiveTransactionCount = getInt(applicationProperties,"excessiveTransactionCount", "10");
        this.scamDetectorSmallAmount = getInt(applicationProperties,"scamDetectorSmallAmount", "1");
        this.scamDetectorLargeAmount = getInt(applicationProperties,"scamDetectorLargeAmount", "900");
        this.scamDetectorTime = getInt(applicationProperties,"scamDetectorTime", "30");
    }

    private static int getInt(final Properties applicationProperties, String key, String defaultValue) {
        return Integer.parseInt(applicationProperties.getProperty(key, defaultValue));
    }

    public String getTransactionsStream() {
        return transactionsStream;
    }

    public String getLockedCardsStream() {
        return lockedCardsStream;
    }

    public String getAlertsStream() {
        return alertsStream;
    }

    public String getRegion() {
        return region;
    }

    public int getExcessiveTransactionWindow() {
        return excessiveTransactionWindow;
    }

    public int getExcessiveTransactionCount() {
        return excessiveTransactionCount;
    }

    public int getScamDetectorSmallAmount() {
        return scamDetectorSmallAmount;
    }

    public int getScamDetectorLargeAmount() {
        return scamDetectorLargeAmount;
    }

    public int getScamDetectorTime() {
        return scamDetectorTime;
    }
}
