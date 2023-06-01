package com.cloudsoft.model;

import java.util.Objects;

public class LockedCard {
    private String cardNumber;
    private String customerId;

    public LockedCard() {
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LockedCard)) return false;
        LockedCard that = (LockedCard) o;
        return Objects.equals(getCardNumber(), that.getCardNumber()) && Objects.equals(getCustomerId(), that.getCustomerId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCardNumber(), getCustomerId());
    }

    @Override
    public String toString() {
        return "LockedCard{" +
                "cardNumber='" + cardNumber + '\'' +
                ", customerId='" + customerId + '\'' +
                '}';
    }
}
