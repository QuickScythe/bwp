package com.bwp.utils;

import org.json.JSONObject;

public record Feedback<T>(Result result, T payload, String message) {


    public String json() {
        return new JSONObject()
                .put("result", result.name())
                .put("payload", payload)
                .put("message", message)
                .toString();
    }

    public String raw() {
        return result + ": " + message + "\nPayload: " + payload;
    }

    public String xml(){
        return "<feedback><result>" + result + "</result><message>" + message + "</message><payload>" + payload + "<payload></feedback>";
    }

    public enum Result {
        SUCCESS,
        NO_ACTION,
        FAILURE
    }

}
