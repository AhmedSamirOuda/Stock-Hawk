package com.udacity.stockhawk.event;

/**
 * Created by Ahmed on 3/20/17.
 */

public class NotFound {
    private String message;
    private String symbol;

    public NotFound(String message, String symbol) {
        this.message = message;
        this.symbol = symbol;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
