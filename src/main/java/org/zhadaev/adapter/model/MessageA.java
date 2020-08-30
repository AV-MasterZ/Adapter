package org.zhadaev.adapter.model;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class MessageA {

    private String msg;
    private String lng;
    private Coordinates coordinates;

    public String getMsg() {
        return msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(final String lng) {
        this.lng = lng;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(final Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageA)) return false;
        MessageA messageA = (MessageA) o;
        return Objects.equals(msg, messageA.msg) &&
                Objects.equals(lng, messageA.lng) &&
                Objects.equals(coordinates, messageA.coordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(msg, lng, coordinates);
    }
}
