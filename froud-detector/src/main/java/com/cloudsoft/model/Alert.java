package com.cloudsoft.model;

import java.util.Objects;

public class Alert {
    private String type;
    private String msg;

    public Alert() {
    }

    public Alert(String type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Alert)) return false;
        Alert alert = (Alert) o;
        return Objects.equals(getType(), alert.getType()) && Objects.equals(getMsg(), alert.getMsg());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getMsg());
    }

    @Override
    public String toString() {
        return "Alert{" +
                "type='" + type + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
