package org.zhadaev.adapter.model;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

@Component
public class MessageB {

    private String txt;
    private Instant createdDt;
    private int currentTemp;

    public String getTxt() {
        return txt;
    }

    public void setTxt(final String txt) {
        this.txt = txt;
    }

    public Instant getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(final Instant createdDt) {
        this.createdDt = createdDt;
    }

    public int getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(final int currentTemp) {
        this.currentTemp = currentTemp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageB)) return false;
        MessageB messageB = (MessageB) o;
        return currentTemp == messageB.currentTemp &&
                Objects.equals(txt, messageB.txt) &&
                Objects.equals(createdDt, messageB.createdDt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(txt, createdDt, currentTemp);
    }
}
