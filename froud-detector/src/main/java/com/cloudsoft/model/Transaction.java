package com.cloudsoft.model;

import java.util.Objects;

public class Transaction {
    private long transactionTs;
    private String transactionId;
    private String cardNumber;
    private String customerId;
    private String atmId;
    private int amount;

    public Transaction() {
    }

    public void setTransactionTs(long transactionTs) {
        this.transactionTs = transactionTs;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setAtmId(String atmId) {
        this.atmId = atmId;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getTransactionTs() {
        return transactionTs;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getAtmId() {
        return atmId;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        Transaction that = (Transaction) o;
        return getTransactionTs() == that.getTransactionTs() && Objects.equals(getTransactionId(), that.getTransactionId()) && Objects.equals(getCardNumber(), that.getCardNumber()) && Objects.equals(getCustomerId(), that.getCustomerId()) && Objects.equals(getAtmId(), that.getAtmId()) && Objects.equals(getAmount(), that.getAmount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTransactionTs(), getTransactionId(), getCardNumber(), getCustomerId(), getAtmId(), getAmount());
    }

    public String toCsv() {
        return transactionTs + ";" +
                transactionId + ";" +
                cardNumber + ";" +
                customerId + ";" +
                atmId + ";" +
                amount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionTs=" + transactionTs +
                ", transactionId='" + transactionId + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", customerId='" + customerId + '\'' +
                ", atmId='" + atmId + '\'' +
                ", amount=" + amount +
                '}';
    }
}
