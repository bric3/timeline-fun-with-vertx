package com.brice;

import java.io.Serializable;

public class Yay implements Serializable {
    private String message;

    public Yay(String message) {
        this.message = message;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Yay{");
        sb.append("message='").append(message).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
