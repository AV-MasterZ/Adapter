package org.zhadaev.adapter.model;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
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

    public String getCreatedDt() {
        return createdDt == null ? null : createdDt.toString();
    }

    public void setCreatedDt(final String createdDt) {
        TemporalAccessor ta = DateTimeFormatter.ISO_INSTANT.parse(createdDt);
        this.createdDt = Instant.from(ta);
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
