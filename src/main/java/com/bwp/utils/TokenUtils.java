package com.bwp.utils;

import com.bwp.data.account.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TokenUtils {

    public static final long TOKEN_EXPIRATION_TIME = TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS);
    private static final Map<String, Token> tokens = new HashMap<>();

    public static Token request(User user) {
        if (user == null)
            return null; // User not found or invalid credentials
        for(Token token : tokens.values()) {
            if (token.user().equals(user)) {
                if (!token.expired()) {
                    Utils.integration().logger().log("Returning existing token for user: {}", user.username());
                    return token; // Return existing valid token
                } else {
                    tokens.remove(token.secret()); // Remove expired token
                }
            }
        }
        Token token = new Token(user);
        tokens.put(token.secret(), token);
        return token;
    }

    public static Feedback<String> validate(String secret) {
        Token token = tokens.get(secret);
        if (token == null) {
            return new Feedback<>(Feedback.Result.FAILURE, "Invalid token", "Token not found");
        }
        if (token.expired()) {
            tokens.remove(secret); // Remove expired token
            return new Feedback<>(Feedback.Result.NO_ACTION, "Token expired", "Token has expired and has been removed");
        }
        return new Feedback<>(Feedback.Result.SUCCESS, secret, "Token is valid and active");
    }

    public static boolean validate(Token token) {
        if (token == null) {
            return false;
        }
        if (token.expired()) {
            tokens.remove(token.secret()); // Remove expired token
            return false; // Token is expired
        }
        return true; // Token is valid and active
    }
}
