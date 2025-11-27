package com.bwp.utils;

import org.json.JSONObject;

/**
 * Generic structured response wrapper used by APIs and internal operations.
 * <p>
 * Encapsulates a result status, a payload of an arbitrary type, and a human-readable message.
 * Provides helpers to serialize into JSON, raw text, or a simple XML snippet.
 *
 * @param <T> the type of the payload
 */
public record Feedback<T>(Result result, T payload, String message) {

    /**
     * Serializes this feedback to a compact JSON string.
     *
     * @return JSON representation containing result, payload, and message fields
     */
    public String json() {
        return new JSONObject()
                .put("result", result.name())
                .put("payload", payload)
                .put("message", message)
                .toString();
    }

    /**
     * Produces a simple human-readable string containing the result and message,
     * followed by the payload on a new line.
     *
     * @return a raw text representation
     */
    public String raw() {
        return result + ": " + message + "\nPayload: " + payload;
    }

    /**
     * Generates a minimal XML snippet containing the result, message, and payload.
     * Note: This is intended for diagnostics and is not a full XML schema.
     *
     * @return XML representation as a string
     */
    public String xml(){
        return "<feedback><result>" + result + "</result><message>" + message + "</message><payload>" + payload + "<payload></feedback>";
    }

    /**
     * Outcome of an operation represented by Feedback.
     */
    public enum Result {
        /** Operation completed successfully. */
        SUCCESS,
        /** Operation performed no changes or had nothing to do. */
        NO_ACTION,
        /** Operation failed. */
        FAILURE
    }

}
